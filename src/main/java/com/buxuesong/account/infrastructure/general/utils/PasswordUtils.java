package com.buxuesong.account.infrastructure.general.utils;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * USERS 数据表密码加密器 将生成的密码存入数据表后，即可在登录中使用该密码
 */
public class PasswordUtils {
    public static void main(String[] args) {
        System.out.println(generatePassword("12345"));
    }

    public static String generatePassword(String password) {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }
}
