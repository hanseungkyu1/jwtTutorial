package me.seungkyu.tutorial.controller;

import me.seungkyu.tutorial.dto.LoginDto;
import me.seungkyu.tutorial.dto.TokenDto;
import me.seungkyu.tutorial.jwt.JwtFilter;
import me.seungkyu.tutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

// AuthController는 TokenProvider, AuthenticationManagerBuilder를 주입 받음
// 로그인 API 경로는 /api/authenticate이고 Post요청을 받음
@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // LoginDto의 username, password를 파라미터로 받고, 이를 이용해 토큰객체를 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // authenticationToken을 이용해서 Authentication객체를 생성하려고 authenticate메서드가 실행이 될 때
        // CustomUserDetailsService의 loadUserByUserName 메서드가 실행 되고
        // 이 결과값을 가지고 authentication객체를 생성하게 됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 생성된 객체를 SecurityContext에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 저장된 인증정보를 기준으로 해서 tokenProvider에서 생성한 메서드인 createToken을 통해 JWT 토큰이 생성됨
        String jwt = tokenProvider.createToken(authentication);

        // JWT Token을 Response Header에도 넣어주고
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // TokenDto를 이용해서 Response Body에도 넣어서 리턴
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}