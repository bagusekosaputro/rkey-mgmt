package com.rkey.returnmgmt;

import com.rkey.returnmgmt.model.Order;
import com.rkey.returnmgmt.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
public class InitDatabase {
    private static final Logger log = LoggerFactory.getLogger(InitDatabase.class);

    @Bean
    CommandLineRunner initialize(OrderRepository orderRepository) {
        return args -> {
            log.info("Preloading database");
        };
    }
}
