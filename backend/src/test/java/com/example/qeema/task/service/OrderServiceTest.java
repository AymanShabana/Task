package com.example.qeema.task.service;

import com.example.qeema.task.dto.OrdersDto;
import com.example.qeema.task.entity.Order;
import com.example.qeema.task.entity.Product;
import com.example.qeema.task.entity.User;
import com.example.qeema.task.repository.OrderRepository;
import com.example.qeema.task.repository.ProductRepository;
import com.example.qeema.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testFindAll() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findAll(PageRequest.of(0, 10))).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testCreateOrder() {
        OrdersDto orderDto = new OrdersDto();
        orderDto.setUserId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));
        OrdersDto.OrderItemRequest itemRequest = new OrdersDto.OrderItemRequest();
        itemRequest.setProductId(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"));
        itemRequest.setQuantity(1);
        List<OrdersDto.OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(itemRequest);
        orderDto.setOrderItems(orderItems);

        User user = new User();
        user.setId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));

        Product product = new Product();
        product.setId(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"));

        when(userRepository.findById(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"))).thenReturn(Optional.of(user));
        when(productRepository.findById(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"))).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        Order result = orderService.createOrder(orderDto);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(1, result.getOrderItems().size());
        assertEquals(product, result.getOrderItems().get(0).getProduct());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_UserNotFound() {
        OrdersDto orderDto = new OrdersDto();
        orderDto.setUserId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));

        when(userRepository.findById(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"))).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderDto);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testCreateOrder_ProductNotFound() {
        OrdersDto orderDto = new OrdersDto();
        orderDto.setUserId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));
        OrdersDto.OrderItemRequest itemRequest = new OrdersDto.OrderItemRequest();
        itemRequest.setProductId(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"));
        itemRequest.setQuantity(1);
        List<OrdersDto.OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(itemRequest);
        orderDto.setOrderItems(orderItems);

        User user = new User();
        user.setId(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"));

        when(userRepository.findById(UUID.fromString("ca543d9f-e7b9-4e7f-805c-c78c529d4411"))).thenReturn(Optional.of(user));
        when(productRepository.findById(UUID.fromString("3b275ba8-8667-4d94-b435-9467fca9abdd"))).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderDto);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testFulfillOrder() throws InterruptedException {
        Order order = new Order();
        order.setStatus(Order.OrderStatus.PENDING);

        doAnswer(invocation -> {
            Order arg = invocation.getArgument(0);
            arg.setStatus(Order.OrderStatus.COMPLETED);
            return null;
        }).when(orderRepository).save(any(Order.class));

        orderService.fulfillOrder(order);

        Thread.sleep(6000);

        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
