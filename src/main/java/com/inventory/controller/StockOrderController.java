package com.inventory.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inventory.common.BusinessException;
import com.inventory.common.RequirePermission;
import com.inventory.common.Result;
import com.inventory.dto.StockOrderRequest;
import com.inventory.entity.StockOrder;
import com.inventory.mapper.StockOrderMapper;
import com.inventory.service.StockOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-orders")
public class StockOrderController {

    @Autowired
    private StockOrderService stockOrderService;

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @PostMapping("/inbound")
    @RequirePermission("stock:inbound")
    public Result<String> inbound(@RequestBody StockOrderRequest req, HttpServletRequest request) {
        StockOrder order = stockOrderService.createOrder(1, req, (String) request.getAttribute("username"));
        return Result.success("入库成功，单号: " + order.getOrderNo());
    }

    @PostMapping("/outbound")
    @RequirePermission("stock:outbound")
    public Result<String> outbound(@RequestBody StockOrderRequest req, HttpServletRequest request) {
        StockOrder order = stockOrderService.createOrder(2, req, (String) request.getAttribute("username"));
        return Result.success("出库成功，单号: " + order.getOrderNo());
    }

    @GetMapping("/{id}")
    @RequirePermission("stock:view")
    public Result<StockOrder> detail(@PathVariable Long id) {
        StockOrder order = stockOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "单据不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/page")
    @RequirePermission("stock:view")
    public Result<PageInfo<StockOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        List<StockOrder> list = stockOrderMapper.selectOrderList(type);
        return Result.success(new PageInfo<>(list));
    }
}