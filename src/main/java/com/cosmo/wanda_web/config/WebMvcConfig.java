package com.cosmo.wanda_web.config;

import com.cosmo.wanda_web.infra.SessionEventInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessionEventInterceptor sessionEventInterceptor;

    public WebMvcConfig(SessionEventInterceptor sessionEventInterceptor) {
        this.sessionEventInterceptor = sessionEventInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionEventInterceptor);
    }
}