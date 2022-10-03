package me.seungkyu.tutorial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.seungkyu.tutorial.entity.Authority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity // 데이터베이스의 테이블과 1:1 매핑되는 객체
@Table(name = "`users`") // 테이블명을 users로 지정하기 위함
// 아래 어노테이션들은 lombok 어노테이션으로 get, set, builder, constructor 관련 코드를 자동으로 생성
// lombok 어노테이션을 실무에서 사용할 때에는 여러가지 사항들을 고려하여 신중히 사용
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // userId라는 자동 증가 되는 PK
    @Id // PK
    @Column(name = "user_id") // user_id라는
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    // @ManyToMany, @JoinTable 어노테이션은 User객체와 권한객체의 다대다 관계를 일대다, 다대일 관게의 조인테이블로 정의 했다는 뜻
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}