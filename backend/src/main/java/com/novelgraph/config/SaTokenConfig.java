package com.novelgraph.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 * 注册全局拦截器，对除 /auth/** 外的接口强制登录校验。
 *
 * @author novelgraph
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/register",
                        "/auth/login",
                        // 静态资源 / Swagger / 健康检查
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/favicon.ico",
                        "/error"
                );
    }
}
