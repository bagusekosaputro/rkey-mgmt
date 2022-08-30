package com.rkey.returnmgmt.view.request;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class ReturnRequest {
    @NonNull
    private String orderId;
    @NonNull
    private String emailAddress;
    @NonNull
    private List<ReturnItemRequest> items;
}
