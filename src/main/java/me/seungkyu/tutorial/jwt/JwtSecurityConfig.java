package me.seungkyu.tutorial.jwt;

import me.seungkyu.tutorial.jwt.JwtFilter;
import me.seungkyu.tutorial.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 1. SecurityConfigurerAdapter를 extends하고
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    // 2. TokenProvider를 주입 받아서
    private TokenProvider tokenProvider;

    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // 3. configure메서드를 오버라이딩
    @Override
    public void configure(HttpSecurity http) {
        // 4. 만든 JwtFilter를
        // 5. Security로직에 필터로 등록한다.
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}