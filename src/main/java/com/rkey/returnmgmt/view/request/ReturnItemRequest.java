package com.rkey.returnmgmt.view.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ReturnItemRequest {
    @NonNull
    private String sku;
    @NonNull
    private Long quantity;
    @NonNull
    private Double price;
    @NonNull
    private String itemName;
}
