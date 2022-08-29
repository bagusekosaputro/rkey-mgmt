package com.rkey.returnmgmt.view.exception;

public class PendingReturnNotFoundException extends RuntimeException {
    public PendingReturnNotFoundException(String orderId) {
        super("Pending Return " + orderId + "Not Found.");
    }
}
