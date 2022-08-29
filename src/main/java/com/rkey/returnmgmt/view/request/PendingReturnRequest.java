package com.rkey.returnmgmt.view.request;

import lombok.Getter;

@Getter
public class PendingReturnRequest {
    private String orderId;
    private String emailAddress;
}
