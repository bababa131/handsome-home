package com.inventory.mapper;

import com.inventory.entity.StockTake;
import com.inventory.entity.StockTakeItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StockTakeMapper {

    int insertTake(StockTake take);

    int insertItems(@Param("list") List<StockTakeItem> items);

    StockTake selectById(@Param("id") Long id);

    List<StockTake> selectTakeList();

    List<StockTakeItem> selectItemsByTakeId(@Param("takeId") Long takeId);

    int updateItemActual(StockTakeItem item);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}