package com.inventory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockOrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
}