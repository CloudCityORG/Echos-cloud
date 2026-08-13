package com.murk.servicechart.config;

import com.murk.commonlib.utils.JwtUtils;
import com.murk.commonlib.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 在每个请求进入 Controller 前，把当前用户 ID 写入 ThreadLocal。
 *
 * 优先从 token 解析；若未传 token 但传了 userId（本地/JMeter 测试模式），则直接使用 userId。
 * 请求结束后必须 remove，防止线程池复用导致数据串台。
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getParameter("token");
        String userIdParam = request.getParameter("userId");

        // 1. 优先从 token 解析 userId
        if (token != null && !token.isEmpty()) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                Integer userId = JwtUtils.getUserIdFromToken(token);
                if (userId != null) {
                    ThreadLocalUtil.set(userId);
                    return true;
                }
            } catch (Exception e) {
                // token 无效，继续尝试 userId 参数
            }
        }

        // 2. 测试模式：无 token 但有 userId 参数
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                ThreadLocalUtil.set(Integer.valueOf(userIdParam));
            } catch (NumberFormatException ignored) {
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }
}
