package com.rkey.returnmgmt.repository;

import com.rkey.returnmgmt.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderIdAndEmailAddress(String orderId, String email);
}
