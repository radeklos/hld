package com.caribou.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class AppConfiguration {

    @Value("${app.allowedOrigin}")
    String allowedOrigin;

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration localhost = new CorsConfiguration();
        localhost.setAllowCredentials(true);
        localhost.addAllowedOrigin(allowedOrigin);
        localhost.addAllowedHeader("*");
        localhost.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", localhost);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

}
