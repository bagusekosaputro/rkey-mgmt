package com.rkey.returnmgmt.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Data
@Table(name = "return_orders")
@NoArgsConstructor
public class ReturnOrder {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NonNull
    private String orderId;
    @NonNull
    private String emailAddress;
    @NonNull
    private String status;
    @NonNull
    private Double refundAmount;
}
