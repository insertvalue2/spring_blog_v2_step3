package com.tenco.blog_jpa_step3.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// JPARepository를 상속받아 기본적인 CRUD 메서드를 사용할 수 있다.
// Entity 클래스와 기본 키 타입을 지정한다.
public interface UserJPARepository extends JpaRepository<User, Integer> {

    // findByUsernameAndPassword는 JPA Query Method로, 메서드 이름을 기반으로 쿼리를 자동 생성
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    // findByUsername는 단순히 username으로 User를 조회하는 메서드
    Optional<User> findByUsername(@Param("username") String username);
}