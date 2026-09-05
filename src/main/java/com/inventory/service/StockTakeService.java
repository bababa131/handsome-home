package com.inventory.service;

import com.inventory.common.BusinessException;
import com.inventory.dto.StockTakeSubmitRequest;
import com.inventory.entity.StockTake;
import com.inventory.entity.StockTakeItem;
import com.inventory.mapper.ProductMapper;
import com.inventory.mapper.StockTakeMapper;
import com.inventory.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StockTakeService {

    @Autowired
    private StockTakeMapper stockTakeMapper;

    @Autowired
    private ProductMapper productMapper;

    // 1. 创建盘点单：瞬间冻结所有商品的系统库存（快照）
    @Transactional(rollbackFor = Exception.class)
    public StockTake createTake(String remark, String operator) {
        StockTake take = new StockTake();
        take.setTakeNo("PD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(10, 100));
        take.setStatus(0);
        take.setOperator(operator);
        take.setRemark(remark);
        stockTakeMapper.insertTake(take);

        // 快照：以当前库存生成明细，盘点期间出入库照常进行，互不干扰
        List<Product> products = productMapper.selectProductList(null, null, null, null);
        List<StockTakeItem> items = new ArrayList<>();
        for (Product p : products) {
            StockTakeItem item = new StockTakeItem();
            item.setTakeId(take.getId());
            item.setProductId(p.getId());
            item.setSystemQty(p.getStock());
            items.add(item);
        }
        if (!items.isEmpty()) {
            stockTakeMapper.insertItems(items);
        }
        return take;
    }

    // 2. 提交实盘数量：计算差异（盘盈为正、盘亏为负）
    @Transactional(rollbackFor = Exception.class)
    public void submitTake(Long takeId, StockTakeSubmitRequest req) {
        StockTake take = stockTakeMapper.selectById(takeId);
        if (take == null) {
            throw new BusinessException(404, "盘点单不存在");
        }
        if (take.getStatus() != 0) {
            throw new BusinessException(400, "该盘点单已提交，不能重复盘点");
        }

        Map<Long, Integer> actualMap = req.getItems().stream()
                .collect(Collectors.toMap(StockTakeSubmitRequest.Item::getProductId,
                        StockTakeSubmitRequest.Item::getActualQty));

        for (StockTakeItem item : stockTakeMapper.selectItemsByTakeId(takeId)) {
            Integer actual = actualMap.get(item.getProductId());
            if (actual != null) {
                item.setActualQty(actual);
                item.setDiffQty(actual - item.getSystemQty());
                stockTakeMapper.updateItemActual(item);
            }
        }
        stockTakeMapper.updateStatus(takeId, 1);
    }

    // 3. 调账：按差异修正实际库存（盘盈加、盘亏扣）
    @Transactional(rollbackFor = Exception.class)
    public void applyTake(Long takeId) {
        StockTake take = stockTakeMapper.selectById(takeId);
        if (take == null) {
            throw new BusinessException(404, "盘点单不存在");
        }
        if (take.getStatus() != 1) {
            throw new BusinessException(400, "只有'已盘点'状态的单据才能调账");
        }
        for (StockTakeItem item : stockTakeMapper.selectItemsByTakeId(takeId)) {
            if (item.getDiffQty() == null || item.getDiffQty() == 0) continue;
            int rows = item.getDiffQty() > 0
                    ? productMapper.addStock(item.getProductId(), item.getDiffQty())
                    : productMapper.deductStock(item.getProductId(), -item.getDiffQty());
            if (rows == 0) {
                throw new BusinessException(400, "调账失败：商品 " + item.getProductId() + " 库存不足");
            }
        }
        stockTakeMapper.updateStatus(takeId, 2);
    }
}