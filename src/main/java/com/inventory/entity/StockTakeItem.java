package com.inventory.entity;

import lombok.Data;

@Data
public class StockTakeItem {
    private Long id;
    private Long takeId;
    private Long productId;
    private String productName;   // 详情查询时 JOIN 带出
    private Integer systemQty;    // 系统库存快照
    private Integer actualQty;    // 实盘数量
    private Integer diffQty;      // 差异
}