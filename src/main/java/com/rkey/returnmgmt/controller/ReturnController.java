package com.rkey.returnmgmt.controller;

import com.rkey.returnmgmt.helper.JWTUtils;
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
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ReturnController {
    private static final Logger log = LoggerFactory.getLogger(ReturnController.class);
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    JWTUtils jwtUtils;

    @PostMapping("/pending/return")
    PendingReturnResponse pendingReturn(@RequestBody PendingReturnRequest body) {
        List<Order> orders = getPendingReturn(body.getOrderId(), body.getEmailAddress());
        PendingReturnResponse response = new PendingReturnResponse();
        if (orders.isEmpty()) {
            response.setMessage("Pending Return Not Found");

            response.setToken("");
        } else {
            String token = jwtUtils.generateToken(body);
            response.setMessage("success");
            response.setToken(token);
        }
        return response;
    }

    @PostMapping("/returns")
    ReturnResponse returnOrder(@RequestBody ReturnRequest body) {
        return new ReturnResponse();
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

    private String generateToken(List<Order> orders) {
        String signature = "";
        return signature;
    }
}
