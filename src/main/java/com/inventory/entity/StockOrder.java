package com.inventory.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockOrder {
    private Long id;
    private String orderNo;
    private Integer type;          // 1入库 2出库
    private String operator;
    private String remark;
    private LocalDateTime createdAt;
    private List<StockOrderItem> items;  // 明细列表（查询详情时填充）
}