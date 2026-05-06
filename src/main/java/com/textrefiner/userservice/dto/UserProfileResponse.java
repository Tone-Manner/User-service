package com.textrefiner.userservice.dto;

import com.textrefiner.userservice.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {
    private final String email;
    private final String nickname;
    private final String status;
    private final int chatRoomCount;

    // User 엔티티를 받아서 DTO로 변환해 주는 생성자
    public UserProfileResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.status = user.getStatus().name();
        this.chatRoomCount = user.getChatRoomCount();
    }
}