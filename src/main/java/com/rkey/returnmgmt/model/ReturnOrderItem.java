package com.rkey.returnmgmt.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Data
@Table(name = "return_items")
@NoArgsConstructor
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
    @NonNull
    private String status;
}
