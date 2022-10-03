package me.seungkyu.tutorial.service;

import java.util.Collections;
import java.util.Optional;

import javassist.bytecode.DuplicateMemberException;
import me.seungkyu.tutorial.dto.UserDto;
import me.seungkyu.tutorial.entity.Authority;
import me.seungkyu.tutorial.entity.User;
//import me.seungkyu.tutorial.exception.DuplicateMemberException;
//import me.seungkyu.tutorial.exception.NotFoundMemberException;
import me.seungkyu.tutorial.repository.UserRepository;
import me.seungkyu.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 로직을 수행하는 메서드로써 파라미터로 받은 userDto안에 username을 기준으로 이미 DB에
    // 동일한 username이 저장되어 있는지 찾아보고, 없으면 권한정보와 유저정보를 만든 후
    // 레포지토리의 save 메서드를 통해 DB에 정보를 저장
    @Transactional
    public User signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 여기에서 중요한점은 메서드를 통해 가입한 회원은 USER ROLE을 가지고 있고,
        // data.sql에서 자동 생성되는 admin 계정은 USER, ADMIN 2개의 ROLE을 가지고 있다.
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    // 유저 권한정보를 가져오는 메서드 2개
    // username을 기준으로 정보를 가져오고,
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
//        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    // SecurityContext에 저장된 username의 정보만 가져온다.
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}