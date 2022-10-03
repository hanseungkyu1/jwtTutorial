package me.seungkyu.tutorial.controller;

import me.seungkyu.tutorial.dto.UserDto;
import me.seungkyu.tutorial.entity.User;
import me.seungkyu.tutorial.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/hello")
//    public ResponseEntity<String> hello() {
//        return ResponseEntity.ok("hello");
//    }

    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    // UserDto를 파라미터로 받아서 UserService의 signup메서드를 호출
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.signup(userDto));
    }

    // @PreAuthorize 어노테이션을 통해서 USER, ADMIN 두가지 권한 모두 허용했고,
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Optional<User>> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

    // ADMIN 권한만 호출할 수 있도록 설정
    // 그리고 UserService에서 만들었던 username 파라미터를 기준으로 유저 정보와 권한 정보를 리턴하는 API
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Optional<User>> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
    }
}