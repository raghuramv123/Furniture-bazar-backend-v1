package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    // ── FIXED: defaultAddress → primaryAddress ──
    Optional<Address> findByUserIdAndPrimaryAddressTrue(Long userId);
}