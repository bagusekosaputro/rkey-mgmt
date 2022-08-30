package com.rkey.returnmgmt.view.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ReturnResponse {
    private String message;
    private Map<String, String> data;
}
