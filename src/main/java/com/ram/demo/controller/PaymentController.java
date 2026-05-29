package com.ram.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.PaymentVerifyRequest;
import com.ram.demo.service.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	
    private final PaymentService paymentService;

    
    @PostMapping("/create-order/{orderId}")
    public ResponseEntity<Map<String, Object>> createOrder(@PathVariable Long orderId)
            throws RazorpayException {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(orderId));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody PaymentVerifyRequest req)
            throws RazorpayException {
        paymentService.verifyAndConfirm(req);
        return ResponseEntity.ok("Payment verified and order confirmed");
    }
}