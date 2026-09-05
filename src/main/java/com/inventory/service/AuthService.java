package com.inventory.service;

import com.inventory.common.BusinessException;
import com.inventory.entity.User;
import com.inventory.mapper.RoleMapper;
import com.inventory.mapper.UserMapper;
import com.inventory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private JwtUtils jwtUtils;

    // 登录验证，返回 token
    public String login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        // 2. 验证密码（目前明文，后续改为BCrypt）
        if (!Objects.equals(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        // 4. 查询角色和权限，生成 token
        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = roleMapper.selectPermissionCodesByUserId(user.getId());
        return jwtUtils.generateToken(user.getId(), user.getUsername(), roles, permissions);
    }
}