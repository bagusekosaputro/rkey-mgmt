package com.rkey.returnmgmt.view.response;

import com.rkey.returnmgmt.model.ReturnOrder;
import com.rkey.returnmgmt.model.ReturnOrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetOrderResponse {
    private ReturnOrder order;
    private List<ReturnOrderItem> items;
}
