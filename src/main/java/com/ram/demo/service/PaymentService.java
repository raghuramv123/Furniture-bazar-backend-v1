package com.ram.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ram.demo.dtos.PaymentVerifyRequest;
import com.ram.demo.entity.Order;
import com.ram.demo.entity.Payment;
import com.ram.demo.enums.OrderStatus;
import com.ram.demo.enums.PaymentGateway;
import com.ram.demo.enums.PaymentStatus;
import com.ram.demo.exception.PaymentVerificationException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.OrderRepository;
import com.ram.demo.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final String razorpayKeyId;
    private final String razorpayKeySecret;

    // ── inject @Value through constructor — most reliable approach ──
    @Autowired
    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            @Value("${razorpay.key.id}") String razorpayKeyId,
            @Value("${razorpay.key.secret}") String razorpayKeySecret) {
        this.paymentRepository  = paymentRepository;
        this.orderRepository    = orderRepository;
        this.razorpayKeyId      = razorpayKeyId;
        this.razorpayKeySecret  = razorpayKeySecret;
    }

    // ── STEP 1: Create Razorpay order ─────────────────────────────────
    public Map<String, Object> createRazorpayOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Order not found: " + orderId));

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject options = new JSONObject();
            options.put("amount",
                order.getTotalAmount()
                     .multiply(new BigDecimal("100"))
                     .intValue()); // paise
            options.put("currency", "INR");
            options.put("receipt", order.getOrderNumber());

            com.razorpay.Order rzpOrder = client.orders.create(options);

            // save pending payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .gateway(PaymentGateway.RAZORPAY)
                    .gatewayOrderId(rzpOrder.get("id"))
                    .amount(order.getTotalAmount())
                    .currency("INR")
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", rzpOrder.get("id"));
            response.put("amount",          rzpOrder.get("amount"));
            response.put("currency",        rzpOrder.get("currency"));
            response.put("keyId",           razorpayKeyId);
            response.put("orderNumber",     order.getOrderNumber());
            return response;

        } catch (RazorpayException e) {
            throw new RuntimeException(
                "Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    // ── STEP 2: Verify payment signature ─────────────────────────────
    public void verifyAndConfirm(PaymentVerifyRequest req) {
        try {
            boolean isValid = com.razorpay.Utils.verifyPaymentSignature(
                new JSONObject()
                    .put("razorpay_order_id",  req.getRazorpayOrderId())
                    .put("razorpay_payment_id", req.getRazorpayPaymentId())
                    .put("razorpay_signature",  req.getRazorpaySignature()),
                razorpayKeySecret
            );

            if (!isValid) {
                throw new PaymentVerificationException("Invalid payment signature");
            }

        } catch (RazorpayException e) {
            throw new PaymentVerificationException(
                "Signature verification failed: " + e.getMessage());
        }

        Payment payment = paymentRepository
                .findByGatewayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() ->
                    new ResourceNotFoundException("Payment record not found"));

        payment.setGatewayTransactionId(req.getRazorpayPaymentId());
        payment.setGatewaySignature(req.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setMethod(req.getMethod());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    // ── GET payment by order ──────────────────────────────────────────
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Payment not found for order: " + orderId));
    }
}