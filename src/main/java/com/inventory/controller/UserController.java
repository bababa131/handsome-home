package com.inventory.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inventory.common.BusinessException;
import com.inventory.common.Result;
import com.inventory.entity.User;
import com.inventory.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 1. 根据 ID 查询用户
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(user);
    }

    // 2. 分页查询用户列表（测试 PageHelper）
    @GetMapping("/page")
    public Result<PageInfo<User>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(required = false) String username) {

        // 开启分页（PageHelper 会自动拦截下一个 select 查询）
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectUserList(username);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    // 3. 新增用户（测试事务和 JSON 传参）
    @PostMapping
    public Result<String> addUser(@RequestBody User user) {
        // 简单校验
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        user.setStatus(1); // 默认启用
        int rows = userMapper.insertUser(user);
        if (rows > 0) {
            return Result.success("新增成功，用户ID: " + user.getId());
        } else {
            return Result.error(500, "新增失败");
        }
    }
    // 4. 获取当前登录用户信息（通过 Token 解析）
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        // 从 request 中获取拦截器存入的用户 ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 敏感信息脱敏（不返回密码）
        user.setPassword(null);
        return Result.success(user);
    }
}