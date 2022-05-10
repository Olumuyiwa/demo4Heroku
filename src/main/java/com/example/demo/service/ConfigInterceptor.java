package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigInterceptor implements WebMvcConfigurer {

    private final Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }

}