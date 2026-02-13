package com.tarrina.orders.repository;
import com.tarrina.orders.entity.PartnerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerAssignmentRepository
        extends JpaRepository<PartnerAssignment, Long> {

    List<PartnerAssignment> findByPartner_User_Id(Long userId);

    List<PartnerAssignment> findByOrder_Id(Long orderId);
}

