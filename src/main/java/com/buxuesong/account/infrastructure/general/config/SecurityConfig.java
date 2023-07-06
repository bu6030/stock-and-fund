package com.buxuesong.account.infrastructure.general.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    DataSource dataSource;
    private final static String ACCOUNT_CLIENT_AUTHORITY = "ADMIN";

    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ignore
        web.ignoring().antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**",
            "/i/**", "/resources/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("*")
            .maxAge(1800)
            .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(1))); // 设置缓存时间为 1 小时
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/login", "/chrome/**", "/css/**", "/js/**")
            .permitAll()
            .and()
            .authorizeRequests()
            .anyRequest()
            .hasAuthority(ACCOUNT_CLIENT_AUTHORITY)
            .and()
            .csrf()
            .disable()
            .sessionManagement()
            .disable()
            .cors()
            .and()
            .headers().cacheControl()
            .and()
            .contentTypeOptions();
        http.formLogin(withDefaults());
    }
}
