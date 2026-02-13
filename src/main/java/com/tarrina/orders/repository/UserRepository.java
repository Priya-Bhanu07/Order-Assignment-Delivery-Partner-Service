package com.tarrina.orders.repository;


import com.tarrina.orders.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository

public interface UserRepository extends JpaRepository<User, Long> {
}
