package com.example.qeema.task.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.qeema.task.dto.OrdersDto;
import com.example.qeema.task.entity.Order;
import com.example.qeema.task.entity.OrderItem;
import com.example.qeema.task.entity.Product;
import com.example.qeema.task.entity.User;
import com.example.qeema.task.repository.OrderRepository;
import com.example.qeema.task.repository.ProductRepository;
import com.example.qeema.task.repository.UserRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<Order> findAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size));
    }

    public Order createOrder(OrdersDto order) {
        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrdersDto.OrderItemRequest itemRequest : order.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItems.add(orderItem);
        }
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderItems(orderItems);
        newOrder.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(newOrder);
        fulfillOrder(newOrder);
        return newOrder;
    }

    @Async
    public void fulfillOrder(Order order) {
        try {
            Thread.sleep(5000);
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
