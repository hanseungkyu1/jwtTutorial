package me.seungkyu.tutorial.config;

import me.seungkyu.tutorial.jwt.JwtAccessDeniedHandler;
import me.seungkyu.tutorial.jwt.JwtAuthenticationEntryPoint;
import me.seungkyu.tutorial.jwt.JwtSecurityConfig;
import me.seungkyu.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 기본적인 Web 보안을 활성화 하겠다는 의미
// 추가적인 설정을 위해서 WebSecurityConfigurer를 Implements 하거나,
// WebSecurityConfigurerAdapter를 extends 하는 방법이 있음.
// 이번 학습에서는 WebSecurityConfigurerAdapter를 extends 하는 방법을 사용
@EnableWebSecurity
// @PreAuthorize 어노테이션을 메서드 단위로 추가하기 위해서 적용하는 어노테이션을 추가
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 만들었던 jwt 관련 클래스들 생성자 주입
    public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // 패스워드 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2-console 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 로직을 수행하지 않도록 접근할 수 있도록 하는 메서드
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                // ignoring(): 정적인 요청 필터링 제외
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 토큰을 사용하기 때문에 csrf설정은 disable
                .csrf().disable()

                // exception을 핸들링할 때 우리가 만들었던 클래스들을 추가해줌
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2-console을 위한 설정 추가해줌
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 여기에선 세션을 사용하지 않을것이기 때문에 세션 설정을 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // authorizeRequests(): HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다는 의미
                .authorizeRequests()
                // antMatchers(path).permitAll(): /api/hello에 대한 요청은 인증 없이 접근을 허용하겠다는 의미
                .antMatchers("/api/hello").permitAll()
                // 토큰을 받기위한 로그인 api와 // 회원가입을 위한 api는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitall 설정
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                // anyRequest().authenticated(): 그 외에 나머지 요청들은 모두 인증되어야 한다는 의미
                .anyRequest().authenticated()

                // 마지막으로 JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
