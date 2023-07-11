package com.buxuesong.account.infrastructure.general.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    DataSource dataSource;
    private final static String ACCOUNT_CLIENT_AUTHORITY = "ADMIN";

    // 配置basicauth账号密码
    @Bean
    UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
//        users.createUser(User.withUsername("aaa")
//                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("bbb"))
//                .authorities(ACCOUNT_CLIENT_AUTHORITY).build());
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        return jdbcUserDetailsManager;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/fonts/**", "/images/**",
            "/i/**", "/resources/**");
    }

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

    // 配置不同接口访问权限
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 下面配置对/helloWorld1接口需要验证 ADMIN 的 authoritie
            // 和 Controller 中的 @PreAuthorize("hasAuthority('ADMIN')")注解配置效果一样
            // 这两种方式用哪一种都可以
            .authorizeHttpRequests(
                (authorize) -> authorize.requestMatchers("/chrome/**", "/login.html", "/login", "/css/**", "/js/**").permitAll())
            .authorizeHttpRequests(
                    (authorize) -> authorize.requestMatchers("/**").hasAuthority(ACCOUNT_CLIENT_AUTHORITY))
//                .httpBasic(withDefaults())
            .formLogin(withDefaults())
            .logout(withDefaults())
            .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(100))); // 设置缓存时间为 1 小时
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("*")
            .maxAge(1800)
            .allowedOrigins("*");
    }
}
