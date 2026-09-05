package com.inventory.interceptor;

import com.inventory.common.BusinessException;
import com.inventory.common.RequirePermission;
import com.inventory.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 Authorization 头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或 Token 无效");
        }

        String token = authHeader.substring(7);
        try {
            // 验证 token 是否过期
            if (jwtUtils.isTokenExpired(token)) {
                throw new BusinessException(401, "登录已过期，请重新登录");
            }
            // 将用户信息存入 request 属性，方便后续使用
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);
            List<String> roles = jwtUtils.getRolesFromToken(token);
            List<String> permissions = jwtUtils.getPermissionsFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("roles", roles != null ? roles : Collections.emptyList());
            request.setAttribute("permissions", permissions != null ? permissions : Collections.emptyList());
            // 权限注解校验
            if (handler instanceof HandlerMethod handlerMethod) {
                RequirePermission required = handlerMethod.getMethodAnnotation(RequirePermission.class);
                if (required != null) {
                    List<String> userPerms = permissions != null ? permissions : Collections.emptyList();
                    boolean hasPermission = Arrays.stream(required.value())
                            .anyMatch(userPerms::contains);
                    if (!hasPermission) {
                        throw new BusinessException(403, "权限不足，拒绝访问");
                    }
                }
            }
            return true;
        }catch (BusinessException e){
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "Token 无效");
        }
    }
}