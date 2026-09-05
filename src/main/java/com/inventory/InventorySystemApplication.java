package com.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.inventory.mapper")   // 扫描 Mapper 接口所在的包
public class InventorySystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventorySystemApplication.class, args);
    }
}