package com.inventory.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private Integer status;        // 0下架 1上架
    private BigDecimal price;
    private Integer stock;
    private Integer warnThreshold; // 低库存预警阈值
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}