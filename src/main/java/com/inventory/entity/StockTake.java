package com.inventory.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockTake {
    private Long id;
    private String takeNo;
    private Integer status;      // 0待盘点 1已盘点 2已调账
    private String operator;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private List<StockTakeItem> items;
}