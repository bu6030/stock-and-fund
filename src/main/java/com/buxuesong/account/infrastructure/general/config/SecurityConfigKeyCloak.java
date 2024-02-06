package com.buxuesong.account.infrastructure.general.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Keycloak对应的Security文件，如果没有搭建Keycloak服务，将本文件注释，启用另一个SecurityConfig代码
 */
@Order(0)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigKeyCloak {

    private static final String LOGOUT_URL = "http://localhost:8080/realms/myrealm/protocol/openid-connect/logout?redirect_uri=http://localhost:8081/";

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .oauth2Client(withDefaults())
            .oauth2Login((oauth2Login) -> oauth2Login.tokenEndpoint(withDefaults())
                .userInfoEndpoint(withDefaults()))
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests(
                (authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/unauthenticated", "/oauth2/**", "/login/**", "/login.html").permitAll()
                    .anyRequest().hasAnyAuthority("OIDC_USER", "ADMIN"))
            .logout((logout) -> logout.logoutSuccessUrl(LOGOUT_URL));
        return http.build();
    }
}