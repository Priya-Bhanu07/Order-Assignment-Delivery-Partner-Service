package com.tarrina.orders.repository;

import com.tarrina.orders.entity.DeliveryPartner;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DeliveryPartnerRepository
        extends JpaRepository<DeliveryPartner, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from DeliveryPartner p where p.id = :id")
    Optional<DeliveryPartner> findByIdForUpdate(@Param("id") Long id);

    Optional<DeliveryPartner> findByUser_Id(Long userId);

    @Query("""
        SELECT p FROM DeliveryPartner p
        WHERE p.availabilityStatus = 'available'
          AND p.deletedAt IS NULL
    """)
    List<DeliveryPartner> findAvailablePartners();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM DeliveryPartner p WHERE p.id = :id")
    Optional<DeliveryPartner> lockById(Long id);


    Optional<DeliveryPartner> findByUserId(Long userId);

    List<DeliveryPartner> findByAvailabilityStatus(String status);




}
