package com.rkey.returnmgmt.view.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ReturnOrderResponse {
    private String message;
    private Map<String, Object> data;
}
