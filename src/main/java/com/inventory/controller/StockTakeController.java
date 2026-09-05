package com.inventory.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inventory.common.BusinessException;
import com.inventory.common.RequirePermission;
import com.inventory.common.Result;
import com.inventory.dto.StockTakeSubmitRequest;
import com.inventory.entity.Product;
import com.inventory.entity.StockTake;
import com.inventory.mapper.StockTakeMapper;
import com.inventory.service.StockTakeService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-takes")
public class StockTakeController {

    @Autowired
    private StockTakeService stockTakeService;

    @Autowired
    private StockTakeMapper stockTakeMapper;

    // 创建盘点单（生成库存快照）
    @PostMapping
    @RequirePermission("stock:take")
    public Result<String> create(@RequestParam(required = false) String remark,
                                 HttpServletRequest request) {
        StockTake take = stockTakeService.createTake(remark, (String) request.getAttribute("username"));
        return Result.success("盘点单创建成功，单号: " + take.getTakeNo());
    }

    // 提交实盘数量
    @PutMapping("/{id}/submit")
    @RequirePermission("stock:take")
    public Result<String> submit(@PathVariable Long id, @RequestBody StockTakeSubmitRequest req) {
        stockTakeService.submitTake(id, req);
        return Result.success("盘点提交成功");
    }

    // 按差异调账
    @PostMapping("/{id}/apply")
    @RequirePermission("stock:take")
    public Result<String> apply(@PathVariable Long id) {
        stockTakeService.applyTake(id);
        return Result.success("调账完成，库存已按差异修正");
    }

    @GetMapping("/{id}")
    @RequirePermission("stock:take")
    public Result<StockTake> detail(@PathVariable Long id) {
        StockTake take = stockTakeMapper.selectById(id);
        if (take == null) {
            throw new BusinessException(404, "盘点单不存在");
        }
        return Result.success(take);
    }

    @GetMapping("/page")
    @RequirePermission("stock:take")
    public Result<PageInfo<StockTake>> page(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return Result.success(new PageInfo<>(stockTakeMapper.selectTakeList()));
    }
}
