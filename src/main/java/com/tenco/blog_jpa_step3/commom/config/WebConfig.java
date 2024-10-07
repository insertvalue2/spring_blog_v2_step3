package com.tenco.blog_jpa_step3.commom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 인터셉터 적용
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/protected/**")
                .excludePathPatterns("/public/**", "/login", "/logout");

        // 관리자 인터셉터 적용
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**"); // /admin/** 경로에만 관리자 인터셉터 적용}
    }
}