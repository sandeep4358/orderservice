package com.example.Orderservice.controller;

import com.example.Orderservice.dto.OrderRequest;
import com.example.Orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrderOf")
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Placing Order");
        orderService.placeOrder(orderRequest);
        log.info("Sending Order Details with Order Id {} to Notification Service");
        return "Order placed successfully." ;
    }

    @GetMapping("/test")
    public String test(){
        log.info("inside the test method");
        return "order service hit successfully.";
    }

}
