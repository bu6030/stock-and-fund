package com.buxuesong.account.infrastructure.general.utils;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserUtils {
    public static String getUsername(){
        String username = null;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
            username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } else {
            username = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("preferred_username");
        }
        return username;
    }
}