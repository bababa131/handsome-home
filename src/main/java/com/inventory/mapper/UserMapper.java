package com.inventory.mapper;

import com.inventory.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {

    // 根据 ID 查询
    User selectById(@Param("id") Long id);

    // 根据用户名查询（用于登录）
    User selectByUsername(@Param("username") String username);

    // 分页条件查询（支持模糊搜索）
    List<User> selectUserList(@Param("username") String username);

    // 新增用户（返回自增主键）
    int insertUser(User user);

    // 更新用户
    int updateUser(User user);

    // 逻辑删除
    int deleteById(@Param("id") Long id);
}