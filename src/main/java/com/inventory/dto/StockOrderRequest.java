package com.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class StockOrderRequest {
    private String remark;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}