package com.ram.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.AddressRequest;
import com.ram.demo.entity.Address;
import com.ram.demo.security.UserPrincipal;
import com.ram.demo.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAll(Authentication auth) {
        return ResponseEntity.ok(
            addressService.getUserAddresses(getUserId(auth)));
    }

    @PostMapping
    public ResponseEntity<Address> add(
            @Valid @RequestBody AddressRequest req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(getUserId(auth), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest req,
            Authentication auth) {
        return ResponseEntity.ok(
            addressService.updateAddress(id, getUserId(auth), req));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<Void> setDefault(@PathVariable Long id,
                                            Authentication auth) {
        addressService.setDefault(id, getUserId(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Authentication auth) {
        addressService.delete(id, getUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication auth) {
        return ((UserPrincipal) auth.getPrincipal()).getId();
    }
}