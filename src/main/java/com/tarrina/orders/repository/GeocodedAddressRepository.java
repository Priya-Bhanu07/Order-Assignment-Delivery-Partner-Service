package com.tarrina.orders.repository;


import com.tarrina.orders.entity.GeocodedAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeocodedAddressRepository
            extends JpaRepository<GeocodedAddress, Long> {

        Optional<GeocodedAddress> findByAddress(String address);
    }
