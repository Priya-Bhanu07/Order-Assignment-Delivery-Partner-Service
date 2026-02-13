package com.tarrina.orders.repository;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUuid(UUID uuid);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(String status);

    List<Order> findByAssignedPartnerAndStatusIn(
            User user,
            List<String> statuses
    );

    @Query("""
    SELECT o FROM Order o
    WHERE (:status IS NULL OR o.status = :status)
      AND (:contactId IS NULL OR o.contact.id = :contactId)
      AND (:partnerId IS NULL OR o.assignedPartner.id = :partnerId)
""")
    Page<Order> findByFilters(
            @Param("status") String status,
            @Param("contactId") Long contactId,
            @Param("partnerId") Long partnerId,
            Pageable pageable
    );
    List<Order> findByAssignedPartner(User assignedPartner);

    @Query("""
SELECT o FROM Order o
WHERE o.assignedPartner IS NULL
AND o.status = 'received'
""")
    List<Order> findUnassignedOrders();


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(Long id);

    @Query("""
   SELECT o FROM Order o 
   WHERE o.assignedPartner.id = :userId 
    AND o.status IN ('assigned','dispatched')
   """)
    List<Order> findActiveOrdersByPartnerUserId(@Param("userId") Long userId);

    List<Order> findByStatusIn(List<String> statuses);

    List<Order> findByAssignedPartnerId(Long userId);

    List<Order> findByStatusAndCreatedAtBefore(
            String status, LocalDateTime time
    );

    @Query("""
    SELECT o
    FROM Order o
    WHERE o.assignedPartner IS NULL
      AND o.status = 'received'
      AND o.deletedAt IS NULL
""")
    List<Order> findActiveUnassignedOrders();
}
