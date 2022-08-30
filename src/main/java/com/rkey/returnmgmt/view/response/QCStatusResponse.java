package com.rkey.returnmgmt.view.response;

import com.rkey.returnmgmt.model.ReturnOrderItem;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QCStatusResponse {
    private String message;
    private ReturnOrderItem data;
}
