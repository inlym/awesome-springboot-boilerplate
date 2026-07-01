package com.example.system.echo.controller;

import com.example.core.constants.ContextKeys;
import com.example.core.service.I18nService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 请求回显控制器
 *
 * <h2>控制器说明
 * <p>提供 HTTP 请求信息回显接口，用于调试客户端发送的完整请求数据。
 *
 * @module 系统运维
 * @folder 系统运维
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
public class RequestEchoController {

    /** 国际化服务 */
    private final I18nService i18nService;

    // ================================ public 方法 ================================

    /**
     * 获取请求详情并原样回显
     * 提取 HTTP 请求的方法、路径、查询字符串、客户端 IP、请求头、查询参数和请求体，封装后原样返回。
     *
     * @param request HTTP 请求对象
     * @param body    请求体内容，允许为空
     * @return 请求详情回显数据
     */
    @RequestMapping("/echo/request")
    public Map<String, Object> echoRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        // 收集请求头
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        // 从请求属性获取反向代理解析后的真实客户端 IP，dev 环境未启用解析时为 null
        String clientIp = (String) request.getAttribute(ContextKeys.CLIENT_IP);

        // 解析请求上下文的语言环境和当前语言名称
        Locale locale = LocaleContextHolder.getLocale();
        String language = i18nService.getMessage("language.current");

        // 组装回显数据
        Map<String, Object> result = new HashMap<>();
        result.put("method", request.getMethod());
        result.put("path", request.getRequestURI());
        result.put("queryString", request.getQueryString());
        result.put("remoteAddr", request.getRemoteAddr());
        result.put("clientIp", clientIp);
        result.put("locale", locale);
        result.put("language", language);
        result.put("headers", headers);
        result.put("query", request.getParameterMap());
        result.put("body", body);

        return result;
    }
}
