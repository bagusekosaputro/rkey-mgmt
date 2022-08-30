package com.rkey.returnmgmt.repository;

import com.rkey.returnmgmt.model.ReturnOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {
    List<ReturnOrderItem> findByOrderId(Long orderId);
    ReturnOrderItem findByIdAndOrderId(Long id, Long orderId);
    ReturnOrderItem findByOrderIdAndSku(Long orderId, String sku);
}
