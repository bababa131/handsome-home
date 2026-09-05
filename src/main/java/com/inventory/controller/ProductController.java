package com.inventory.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inventory.common.BusinessException;
import com.inventory.common.RequirePermission;
import com.inventory.common.Result;
import com.inventory.entity.Product;
import com.inventory.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return Result.success(product);
    }
    // 低库存预警列表（库存 <= 预警阈值的商品，按库存升序）
    @GetMapping("/low-stock")
    @RequirePermission("product:view")
    public Result<List<Product>> lowStock() {
        return Result.success(productMapper.selectLowStockList());
    }
    // 多条件组合分页查询（索引优化的测试接口）
    @GetMapping("/page")
    @RequirePermission("product:view")
    public Result<PageInfo<Product>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.selectProductList(categoryId, status, startTime, endTime);
        return Result.success(new PageInfo<>(list));
    }

    @PostMapping
    @RequirePermission("product:add")
    public Result<String> add(@RequestBody Product product) {
        if (product.getName() == null || product.getCategoryId() == null) {
            throw new BusinessException(400, "商品名称和分类不能为空");
        }
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getWarnThreshold() == null) product.setWarnThreshold(10);
        productMapper.insertProduct(product);
        return Result.success("新增成功，商品ID: " + product.getId());
    }

    @PutMapping("/{id}")
    @RequirePermission("product:update")
    public Result<String> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        int rows = productMapper.updateProduct(product);
        return rows > 0 ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    @RequirePermission("product:delete")
    public Result<String> delete(@PathVariable Long id) {
        int rows = productMapper.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}