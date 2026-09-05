package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestParam String username,
                                             @RequestParam String password) {
        String token = authService.login(username, password);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");
        return Result.success(data);
    }
}