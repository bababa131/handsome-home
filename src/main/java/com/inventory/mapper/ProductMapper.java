package com.inventory.mapper;

import com.inventory.entity.Product;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductMapper {

    Product selectById(@Param("id") Long id);

    // 多条件组合查询（分类 + 状态 + 时间范围）—— 索引优化的主角
    List<Product> selectProductList(@Param("categoryId") Long categoryId,
                                    @Param("status") Integer status,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteById(@Param("id") Long id);

    // 出库扣减（乐观锁：库存不足时影响行数为 0）
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // 入库增加
    int addStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    // 低库存预警：库存 <= 预警阈值
    List<Product> selectLowStockList();
}
