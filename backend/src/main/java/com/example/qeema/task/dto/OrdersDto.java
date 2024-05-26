package com.example.qeema.task.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class OrdersDto {

    @NotNull
    private UUID userId;

    @NotEmpty
    private List<OrderItemRequest> orderItems;

    public OrdersDto() {
    }

    public OrdersDto(UUID userId, List<OrderItemRequest> orderItems) {
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    public OrdersDto userId(UUID userId) {
        setUserId(userId);
        return this;
    }

    public OrdersDto orderItems(List<OrderItemRequest> orderItems) {
        setOrderItems(orderItems);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof OrdersDto)) {
            return false;
        }
        OrdersDto ordersDto = (OrdersDto) o;
        return Objects.equals(userId, ordersDto.userId) && Objects.equals(orderItems, ordersDto.orderItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, orderItems);
    }

    @Override
    public String toString() {
        return "{" +
            " userId='" + getUserId() + "'" +
            ", orderItems='" + getOrderItems() + "'" +
            "}";
    }


    public static class OrderItemRequest {
        @NotNull
        private UUID productId;

        @NotNull
        @Min(1)
        private int quantity;

        public UUID getProductId(){
            return this.productId;
        }

        public void setProductId(UUID productId){
            this.productId = productId;
        }

        public int getQuantity(){
            return this.quantity;
        }

        public void setQuantity(int quantity){
            this.quantity = quantity;
        }
    }
}
