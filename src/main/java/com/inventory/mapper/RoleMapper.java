package com.inventory.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    //查询用户的角色编码列表
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
    //查用户的权限码列表(用户-角色-权限 两跳关联)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}