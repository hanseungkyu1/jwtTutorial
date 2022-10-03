package me.seungkyu.tutorial.repository;

import me.seungkyu.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository를 extends하는 것으로 findAll이나 save같은 메서드를 기본적으로 사용할 수 있음
public interface UserRepository extends JpaRepository<User, Long> {

    // @EntityGraph 어노테이션은 해당 쿼리가 수행될 때 Lazy조회가 아니고 Eager조회로 authorities정보를 같이 가져옴
    @EntityGraph(attributePaths = "authorities")
    // 유저네임을 기준으로 해서 유저 정보를 권한정보와 같이 가져오는 메서드
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
