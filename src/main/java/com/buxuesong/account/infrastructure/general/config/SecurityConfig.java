package com.buxuesong.account.infrastructure.general.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

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
                (authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/unauthenticated", "/oauth2/**", "/login/**").permitAll()
                    .anyRequest().hasAnyAuthority("OIDC_USER"))
            .logout((logout) -> logout.logoutSuccessUrl(LOGOUT_URL));
        return http.build();
    }
}