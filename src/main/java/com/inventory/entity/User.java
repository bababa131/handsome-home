package com.inventory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;        // 0禁用 1启用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;       // 逻辑删除标记
}