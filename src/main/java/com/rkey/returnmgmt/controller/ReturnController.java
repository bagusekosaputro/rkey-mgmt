package com.rkey.returnmgmt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkey.returnmgmt.config.JWTUtils;
import com.rkey.returnmgmt.enums.QCEnum;
import com.rkey.returnmgmt.enums.ReturnStatus;
import com.rkey.returnmgmt.model.Order;
import com.rkey.returnmgmt.model.ReturnOrder;
import com.rkey.returnmgmt.model.ReturnOrderItem;
import com.rkey.returnmgmt.repository.ReturnOrderItemRepository;
import com.rkey.returnmgmt.repository.ReturnOrderRepository;
import com.rkey.returnmgmt.view.request.PendingReturnRequest;
import com.rkey.returnmgmt.view.request.QCStatusRequest;
import com.rkey.returnmgmt.view.request.ReturnItemRequest;
import com.rkey.returnmgmt.view.request.ReturnRequest;
import com.rkey.returnmgmt.view.response.GetOrderResponse;
import com.rkey.returnmgmt.view.response.QCStatusResponse;
import com.rkey.returnmgmt.view.response.ReturnOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RestController
public class ReturnController {
    private static final Logger log = LoggerFactory.getLogger(ReturnController.class);

    @Autowired
    ReturnOrderRepository returnOrderRepository;
    @Autowired
    ReturnOrderItemRepository returnOrderItemRepository;
    @Autowired
    JWTUtils jwtUtils;

    @PostMapping(value = "/pending/return")
    Map<String, String> pendingReturn(@RequestBody PendingReturnRequest body) {
        List<Order> orders = getPendingReturn(body.getOrderId(), body.getEmailAddress());
        log.info("Order : {}", orders);
        Map<String, String> response = new HashMap<>();
        if (orders.isEmpty()) {
            response.put("message","Pending Return Not Found");
            response.put("token", null);
        } else {
            String token = jwtUtils.generateToken(body);
            response.put("message","Success");
            response.put("token", token);
        }
        return response;
    }

    @PostMapping("/returns")
    ResponseEntity<ReturnOrderResponse> returnOrder(@RequestBody ReturnRequest body, @RequestHeader(value = "Authorization") String headerAuth) {
        String parseToken = jwtUtils.parseJWT(headerAuth);
        ReturnOrderResponse response = new ReturnOrderResponse();
        if (parseToken == null) {
            response.setMessage("Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        boolean validToken = jwtUtils.validateToken(parseToken, body.getOrderId(), body.getEmailAddress());
        if (!validToken) {
            response.setMessage("Token Mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        ReturnOrder returnOrder = returnOrderProcess(body);
        Map<String, Object> responseBody = buildReturnOrderResponse(returnOrder);
        response.setMessage("Success");
        response.setData(responseBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/returns/{id}")
    ResponseEntity<Map<String, Object>> getReturnOrder(@PathVariable Long id) {
        Optional<ReturnOrder> validOrder = returnOrderRepository.findById(id);
        ReturnOrderResponse response = new ReturnOrderResponse();
        if (validOrder.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Map<String, Object> responseBody = buildGetReturnOrderData(validOrder.get());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PutMapping("/returns/{id}/items/{itemId}/status")
    ResponseEntity<QCStatusResponse> updateItemStatus(@PathVariable Long id, @PathVariable Long itemId, @RequestBody QCStatusRequest body) {
        ReturnOrderItem item = returnOrderItemRepository.findByIdAndOrderId(itemId, id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        item.setStatus(body.getStatus().toString());
        ReturnOrderItem updateItem = returnOrderItemRepository.save(item);
        QCStatusResponse response = new QCStatusResponse();
        reCalculateRefundAmount(id);
        response.setData(updateItem);
        response.setMessage(HttpStatus.OK.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private List<Order> getPendingReturn(String orderId, String email) {
        List<Order> orders = new ArrayList<>();
        try {
            String filename = "orders.csv";
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] order = line.split(",");
                if (order[0].equals(orderId) && order[1].equals(email)) {
                    orders.add(new Order(order[0],
                            order[1],
                            order[2],
                            Long.parseLong(order[3]),
                            Double.parseDouble(order[4]),
                            order[5]));
                }

            }
            is.close();
        } catch (IOException e) {
            log.info("File not found");
        }
        return orders;
    }

    private List<Order> validateReturnOrder(String orderId, String email, List<String> skus) {
        List<Order> orders = new ArrayList<>();
        try {
            String filename = "orders.csv";
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] order = line.split(",");
                if (order[0].equals(orderId) && order[1].equals(email) && skus.contains(order[2])) {
                    orders.add(new Order(order[0],
                            order[1],
                            order[2],
                            Long.parseLong(order[3]),
                            Double.parseDouble(order[4]),
                            order[5]));
                }

            }
            is.close();
        } catch (IOException e) {
            log.info("File not found");
        }
        return orders;
    }

    private ReturnOrder returnOrderProcess(ReturnRequest body) {
        List<String> skuList = new ArrayList<>();
        for (ReturnItemRequest item : body.getItems()) {
            skuList.add(item.getSku());
        }
        List<Order> orders = validateReturnOrder(body.getOrderId(), body.getEmailAddress(), skuList);
        ReturnOrder result = null;
        if (!orders.isEmpty()) {
            Double refundAmount = 0.00;
            ReturnOrder getOrder = returnOrderRepository.findByOrderIdAndEmailAddress(body.getOrderId(), body.getEmailAddress());
            if (getOrder == null) {
                ReturnOrder newReturnOrder = new ReturnOrder();
                newReturnOrder.setOrderId(body.getOrderId());
                newReturnOrder.setEmailAddress(body.getEmailAddress());
                newReturnOrder.setStatus(ReturnStatus.AWAITING_APPROVAL.name());
                newReturnOrder.setRefundAmount(calculateRefundAmount(refundAmount, body.getItems()));

                result = returnOrderRepository.save(newReturnOrder);
                Long orderId = result.getId();
                saveItems(body.getItems(), orderId);
            } else {
                boolean validItems = validateReturnItems(getOrder.getId(), body.getItems());
                if(!validItems) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item(s) in this order already returned");
                }
                saveItems(body.getItems(), getOrder.getId());
                List<ReturnOrderItem> getItems = returnOrderItemRepository.findByOrderId(getOrder.getId());
                for (ReturnOrderItem item : getItems) {
                    if (!item.getStatus().equals(QCEnum.REJECTED.name())) {
                        refundAmount += (item.getPrice() * item.getQuantity());
                    }
                }
                getOrder.setRefundAmount(refundAmount);
                result = returnOrderRepository.save(getOrder);
            }
        }
        return result;
    }

    private Double calculateRefundAmount(Double refundAmount, List<ReturnItemRequest> returnItems) {
        for(ReturnItemRequest item : returnItems) {
            log.info("Calculate refund amount for item with sku: {}", item.getSku());
            refundAmount += (item.getPrice() * item.getQuantity());
        }

        return refundAmount;
    }

    private Map<String, Object> buildReturnOrderResponse(ReturnOrder returnOrder) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(returnOrder, Map.class);
        return result;
    }

    private Map<String, Object> buildGetReturnOrderData(ReturnOrder returnOrder) {
        ObjectMapper mapper = new ObjectMapper();
        GetOrderResponse data = new GetOrderResponse();
        List<ReturnOrderItem> items = returnOrderItemRepository.findByOrderId(returnOrder.getId());
        data.setOrder(returnOrder);
        data.setItems(items);
        Map<String, Object> result = mapper.convertValue(data, Map.class);

        return result;
    }

    private boolean validateReturnItems(Long orderId, List<ReturnItemRequest> items) {
        log.info("Validate return item(s) in order with Id: {}", orderId);
        boolean result = true;
        for (ReturnItemRequest item : items) {
            ReturnOrderItem isExist = returnOrderItemRepository.findByOrderIdAndSku(orderId, item.getSku());
            if (isExist != null) {
                result = false;
                break;
            }
        }

        return result;
    }

    private void reCalculateRefundAmount(Long orderId) {
        Optional<ReturnOrder> order = returnOrderRepository.findById(orderId);
        Integer updatedStatus = 0;
        if (order.isPresent()) {
            List<ReturnOrderItem> items = returnOrderItemRepository.findByOrderId(orderId);
            Double refundAmount = 0.00;
            for (ReturnOrderItem item : items) {
                if (!item.getStatus().equals(QCEnum.REJECTED.name())) {
                    refundAmount += (item.getPrice() * item.getQuantity());
                }

                if(!item.getStatus().equals(ReturnStatus.AWAITING_APPROVAL.name())) {
                    updatedStatus += 1;
                }
            }
            if (items.size() == updatedStatus) {
                order.get().setStatus(ReturnStatus.COMPLETED.name());
            }
            order.get().setRefundAmount(refundAmount);
            returnOrderRepository.save(order.get());
        }
    }

    private void saveItems(List<ReturnItemRequest> items, Long orderId) {
        for (ReturnItemRequest item : items) {
            ReturnOrderItem newItem = new ReturnOrderItem();
            newItem.setOrderId(orderId);
            newItem.setSku(item.getSku());
            newItem.setQuantity(item.getQuantity());
            newItem.setPrice(item.getPrice());
            newItem.setItemName(item.getItemName());
            newItem.setStatus(ReturnStatus.AWAITING_APPROVAL.name());

            returnOrderItemRepository.save(newItem);
        }
    }
}
