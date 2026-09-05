package com.inventory.service;

import com.inventory.common.BusinessException;
import com.inventory.dto.StockOrderRequest;
import com.inventory.entity.Product;
import com.inventory.entity.StockOrder;
import com.inventory.entity.StockOrderItem;
import com.inventory.mapper.ProductMapper;
import com.inventory.mapper.StockOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class StockOrderService {

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Autowired
    private ProductMapper productMapper;

    // 任何异常都回滚（写单据和动库存必须同生共死）
    @Transactional(rollbackFor = Exception.class)
    public StockOrder createOrder(int type, StockOrderRequest req, String operator) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BusinessException(400, "单据明细不能为空");
        }

        // 1. 插入单据头（回填自增ID）
        StockOrder order = new StockOrder();
        order.setOrderNo(generateOrderNo(type));
        order.setType(type);
        order.setOperator(operator);
        order.setRemark(req.getRemark());
        stockOrderMapper.insertOrder(order);

        // 2. 逐条处理明细：动库存 + 攒明细
        List<StockOrderItem> items = new ArrayList<>();
        for (StockOrderRequest.Item reqItem : req.getItems()) {
            if (reqItem.getQuantity() == null || reqItem.getQuantity() <= 0) {
                throw new BusinessException(400, "商品数量必须大于 0");
            }
            Product product = productMapper.selectById(reqItem.getProductId());
            if (product == null) {
                throw new BusinessException(404, "商品不存在: id=" + reqItem.getProductId());
            }

            // 乐观锁扣减 / 入库累加
            int rows = (type == 1)
                    ? productMapper.addStock(product.getId(), reqItem.getQuantity())
                    : productMapper.deductStock(product.getId(), reqItem.getQuantity());

            if (rows == 0) {
                // 影响行数为 0 → 库存不足 → 抛异常 → 整个事务回滚（单据头也不会留下）
                throw new BusinessException(400,
                        "库存不足，出库失败: " + product.getName()
                                + "（当前库存 " + product.getStock()
                                + "，申请出库 " + reqItem.getQuantity() + "）");
            }

            StockOrderItem item = new StockOrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setQuantity(reqItem.getQuantity());
            items.add(item);
        }

        // 3. 批量插入明细
        stockOrderMapper.insertItems(items);
        return order;
    }

    // 单号：RK/CK + 毫秒时间戳 + 两位随机数
    private String generateOrderNo(int type) {
        String prefix = (type == 1) ? "RK" : "CK";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return prefix + time + ThreadLocalRandom.current().nextInt(10, 100);
    }
}