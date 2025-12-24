package com.example.fitness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 优化版Security配置：彻底放行/api接口 + 解决跨域 + 兼容Spring Boot 2.6.x
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 新增日志（方便排查拦截问题）
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 跨域配置（保留你的终极版，仅补充注释）
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("初始化跨域配置：允许所有域名、方法、请求头");
        CorsConfiguration config = new CorsConfiguration();
        // 1. 允许所有前端域名（开发环境，生产环境替换为具体域名如http://localhost:8081）
        config.addAllowedOriginPattern("*");
        // 2. 允许携带Cookie（前后端分离必备）
        config.setAllowCredentials(true);
        // 3. 放行所有HTTP方法（包含OPTIONS预检请求，避免跨域拦截）
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 4. 放行所有请求头（如Content-Type、Token等）
        config.setAllowedHeaders(Arrays.asList("*"));
        // 5. 暴露所有响应头（前端可获取自定义响应头）
        config.addExposedHeader("*");
        // 6. 预检请求缓存1小时（减少OPTIONS请求次数）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 所有接口生效（包括/api/**）
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("配置Security规则：彻底放行/api/**接口");
        http
                // 1. 优先启用跨域配置（解决跨域拦截）
                .cors().configurationSource(corsConfigurationSource()).and()
                // 2. 关闭CSRF（前后端分离必关，否则POST请求会被拦截）
                .csrf().disable()
                // 3. 关闭Session（无状态，前后端分离用Token）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 4. 放行规则（优先级：精准匹配 > 模糊匹配）
                .authorizeRequests()
                // 精准放行用户信息接口（解决404核心：确保接口不被拦截）
                .antMatchers("/api/user/info/get").permitAll()
                .antMatchers("/api/user/info/save").permitAll()
                // 精准放行身材数据接口
                .antMatchers("/api/body-data/list").permitAll()
                .antMatchers("/api/body-data/save").permitAll()
                .antMatchers("/api/body-data/delete").permitAll()
                // 模糊放行所有/api接口（兜底）
                .antMatchers("/api/**").permitAll()
                // 放行静态资源（若有）
                .antMatchers("/static/**", "/public/**").permitAll()
                // 所有其他请求都放行（开发环境彻底关闭拦截）
                .anyRequest().permitAll()
                // 5. 关闭表单登录/登出/HTTP Basic认证（前后端分离不用）
                .and()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                // 6. 禁用帧选项（避免iframe拦截，可选）
                .headers().frameOptions().disable();
    }
}