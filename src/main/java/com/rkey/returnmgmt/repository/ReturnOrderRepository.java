package com.rkey.returnmgmt.repository;

import com.rkey.returnmgmt.model.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {
    ReturnOrder findByOrderIdAndEmailAddress(String orderId, String emailAddress);
}
