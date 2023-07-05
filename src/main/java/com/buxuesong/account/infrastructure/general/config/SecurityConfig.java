package com.buxuesong.account.infrastructure.general.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource;
    private final static String ACCOUNT_CLIENT_AUTHORITY = "ADMIN";

    //配置basicauth账号密码
    @Bean
    UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
//        users.createUser(User.withUsername("aaa")
//                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("bbb"))
//                .authorities(ACCOUNT_CLIENT_AUTHORITY).build());
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        return jdbcUserDetailsManager;
    }
    // 配置不同接口访问权限
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 下面配置对/helloWorld1接口需要验证 ADMIN 的 authoritie
                // 和 Controller 中的 @PreAuthorize("hasAuthority('ADMIN')")注解配置效果一样
                // 这两种方式用哪一种都可以
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/chrome/**", "/login.html", "/login", "/css/**", "/js/**").permitAll())
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/**").hasAuthority(ACCOUNT_CLIENT_AUTHORITY))
//                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .logout(withDefaults())
                .csrf().disable()
                .build();
    }
}
