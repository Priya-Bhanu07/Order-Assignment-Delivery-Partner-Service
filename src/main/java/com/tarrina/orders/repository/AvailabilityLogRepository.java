package com.tarrina.orders.repository;
import com.tarrina.orders.entity.PartnerAvailabilityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityLogRepository
        extends JpaRepository<PartnerAvailabilityLog, Long> {

    List<PartnerAvailabilityLog> findByPartnerId(Long partnerId);
}
