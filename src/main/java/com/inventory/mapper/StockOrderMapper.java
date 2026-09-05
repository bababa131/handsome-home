package com.inventory.mapper;

import com.inventory.entity.StockOrder;
import com.inventory.entity.StockOrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StockOrderMapper {

    int insertOrder(StockOrder order);

    int insertItems(@Param("list") List<StockOrderItem> items);

    StockOrder selectById(@Param("id") Long id);

    List<StockOrder> selectOrderList(@Param("type") Integer type);
}