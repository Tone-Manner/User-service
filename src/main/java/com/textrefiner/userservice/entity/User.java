package com.textrefiner.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // 보통 user는 예약어라 users로 테이블명을 지정해!
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.FREE; // 기본값은 무료!

    @Column(nullable = false)
    private int chatRoomCount = 0; // 무료 회원의 4개 제한 로직을 위한 카운트
}