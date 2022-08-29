package com.rkey.returnmgmt.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "orders")
public class Order {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NonNull
    private String orderId;
    @NonNull
    private String emailAddress;
    @NonNull
    private String sku;
    @NonNull
    private Long quantity;
    @NonNull
    private Double price;
    @NonNull
    private String itemName;
}
