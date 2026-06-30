package com.example.account.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 VO
 *
 * <h2>说明
 * <p>用于客户端展示的用户信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    /**
     * 用户的显示昵称，用于在界面上展示
     *
     * @example 小明
     */
    private String nickname;
}
