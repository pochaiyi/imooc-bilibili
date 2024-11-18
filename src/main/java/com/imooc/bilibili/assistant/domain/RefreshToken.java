package com.imooc.bilibili.assistant.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;

    private String refreshToken;

    private Long userId;

    private Date createTime;

    public RefreshToken(Long UserId, String refreshToken) {
        super();
        this.userId = UserId;
        this.refreshToken = refreshToken;
        this.createTime = new Date();
    }

}
