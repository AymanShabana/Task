package com.example.qeema.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.qeema.task.dto.OrdersDto;
import com.example.qeema.task.entity.Order;
import com.example.qeema.task.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size){
            Page<Order> orders = orderService.findAll(page, size);
            return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public Order createOrder(@Valid @RequestBody OrdersDto request) {
        return orderService.createOrder(request);
    }

}
