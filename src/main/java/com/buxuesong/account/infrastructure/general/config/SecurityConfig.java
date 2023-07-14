package com.buxuesong.account.infrastructure.general.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final static String ACCOUNT_CLIENT_AUTHORITY = "ADMIN";

    // 配置basicauth账号密码
    @Bean
    UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
//        users.createUser(User.withUsername("aaa")
//                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("bbb"))
//                .authorities(ACCOUNT_CLIENT_AUTHORITY).build());
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @Order(0)
    SecurityFilterChain staticEndpoints(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/css/**", "/js/**", "/fonts/**", "/images/**", "/i/**", "/resources/**")
            .headers((headers) -> headers.cacheControl((cache) -> cache.disable()))
            .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
        return http.build();
    }

    // 配置不同接口访问权限
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 下面配置对/helloWorld1接口需要验证 ADMIN 的 authoritie
            // 和 Controller 中的 @PreAuthorize("hasAuthority('ADMIN')")注解配置效果一样
            // 这两种方式用哪一种都可以
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/login", "/chrome/**").permitAll()
                .anyRequest().hasAuthority(ACCOUNT_CLIENT_AUTHORITY))
            .formLogin((formLogin) -> formLogin.loginProcessingUrl("/").defaultSuccessUrl("/main.html"))
            .logout(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .requestCache(withDefaults())
            .headers(headers -> headers.cacheControl(withDefaults()))
            .build();
    }
}
