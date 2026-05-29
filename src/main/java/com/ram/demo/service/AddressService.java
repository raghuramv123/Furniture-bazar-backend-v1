package com.ram.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ram.demo.dtos.AddressRequest;
import com.ram.demo.entity.Address;
import com.ram.demo.entity.User;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.AddressRepository;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address addAddress(Long userId, AddressRequest req) {
        Address address = new Address();
        mapRequest(userId, req, address);

        // if first address make it primary automatically
        if (addressRepository.findByUserId(userId).isEmpty()) {
            address.setPrimaryAddress(true);
        }

        return addressRepository.save(address);
    }

    public Address updateAddress(Long id, Long userId, AddressRequest req) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Address not found: " + id));

        if (!address.getUser().getId().equals(userId))
            throw new AccessDeniedException("Not your address");

        mapRequest(userId, req, address);
        return addressRepository.save(address);
    }

    public void setDefault(Long id, Long userId) {
        // unset current primary
        addressRepository.findByUserIdAndPrimaryAddressTrue(userId)
                .ifPresent(a -> {
                    a.setPrimaryAddress(false);
                    addressRepository.save(a);
                });

        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Address not found: " + id));

        if (!address.getUser().getId().equals(userId))
            throw new AccessDeniedException("Not your address");

        address.setPrimaryAddress(true);
        addressRepository.save(address);
    }

    public void delete(Long id, Long userId) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Address not found: " + id));

        if (!address.getUser().getId().equals(userId))
            throw new AccessDeniedException("Not your address");

        addressRepository.delete(address);
    }

    private void mapRequest(Long userId, AddressRequest req, Address a) {
        User user = new User();
        user.setId(userId);
        a.setUser(user);
        a.setLabel(req.getLabel());
        a.setFullName(req.getFullName());
        a.setPhone(req.getPhone());
        a.setAddressLine1(req.getAddressLine1());
        a.setAddressLine2(req.getAddressLine2());
        a.setCity(req.getCity());
        a.setState(req.getState());
        a.setPincode(req.getPincode());
        a.setCountry(req.getCountry() != null ? req.getCountry() : "India");
    }
}