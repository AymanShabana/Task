package com.example.qeema.task.controller;

import com.example.qeema.task.dto.OrdersDto;
import com.example.qeema.task.entity.Order;
import com.example.qeema.task.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @MockBean
    private OrderService orderService;

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

    @Test
    public void testGetOrders() throws Exception {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderService.findAll(0, 10)).thenReturn(orderPage);

        mockMvc.perform(get("/orders?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderPage)));
    }

    @Test
    public void testCreateOrder() throws Exception {
        OrdersDto orderDto = new OrdersDto();
        orderDto.setUserId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));
        OrdersDto.OrderItemRequest itemRequest = new OrdersDto.OrderItemRequest();
        itemRequest.setProductId(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"));
        itemRequest.setQuantity(1);
        List<OrdersDto.OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(itemRequest);
        orderDto.setOrderItems(orderItems);

        Order order = new Order();
        when(orderService.createOrder(any(OrdersDto.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(order)));
    }
}
