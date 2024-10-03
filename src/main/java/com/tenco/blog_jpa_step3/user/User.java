package com.tenco.blog_jpa_step3.user;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.tenco.blog_jpa_step3.board.Board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true) // 유니크 제약 조건 설정
    private String username;

    private String password;
    private String email;

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 입력
    private Timestamp createdAt;

    // 단방향, 양방향 매핑(mappedBy)
    // User 엔티티에서 Board 엔티티와의 일대다 관계 설정
    // @OneToMany(mappedBy = "user", fetch = FetchType.EAGER) // 즉시 로딩 설정
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY) // 지연 로딩 설정
    private List<Board> boards; // 사용자가 작성한 게시글 목록

    @Builder
    public User(Integer id, String username, String password, String email, Timestamp createdAt, List<Board> boards) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.boards = boards;
    }
}
