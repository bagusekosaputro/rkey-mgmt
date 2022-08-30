package com.rkey.returnmgmt.controller;

import com.rkey.returnmgmt.config.JWTUtils;
import com.rkey.returnmgmt.model.Order;
import com.rkey.returnmgmt.repository.OrderRepository;
import com.rkey.returnmgmt.view.request.PendingReturnRequest;
import com.rkey.returnmgmt.view.request.QCStatusRequest;
import com.rkey.returnmgmt.view.request.ReturnRequest;
import com.rkey.returnmgmt.view.response.OrderResponse;
import com.rkey.returnmgmt.view.response.PendingReturnResponse;
import com.rkey.returnmgmt.view.response.QCStatusResponse;
import com.rkey.returnmgmt.view.response.ReturnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReturnController {
    private static final Logger log = LoggerFactory.getLogger(ReturnController.class);
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    JWTUtils jwtUtils;

    @PostMapping(value = "/pending/return")
    Map<String, String> pendingReturn(@RequestBody PendingReturnRequest body) {
        List<Order> orders = getPendingReturn(body.getOrderId(), body.getEmailAddress());
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
    ResponseEntity<ReturnResponse> returnOrder(@RequestBody ReturnRequest body, @RequestHeader(value = "Authorization") String headerAuth) {
        String validToken = jwtUtils.parseJWT(headerAuth);
        ReturnResponse response = new ReturnResponse();
        if (validToken == null) {
            response.setMessage("Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("orderId", "1333");
        response.setMessage("Success");
        response.setData(responseBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/returns/{id}")
    OrderResponse getReturnOrder(@PathVariable Long id) {
        return new OrderResponse();
    }

    @PutMapping("/returns/{id}/items/{itemId}/status")
    QCStatusResponse updateItemStatus(@PathVariable Long id, @PathVariable String itemId, @RequestBody QCStatusRequest body) {
        return new QCStatusResponse();
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
}
