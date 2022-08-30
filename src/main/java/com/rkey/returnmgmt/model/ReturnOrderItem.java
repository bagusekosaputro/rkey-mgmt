package com.rkey.returnmgmt.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class ReturnOrderItem {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NonNull
    private Long orderId;
    @NonNull
    private String sku;
    @NonNull
    private Long quantity;
    @NonNull
    private Double price;
    @NonNull
    private String itemName;
    private String status;
}
