package com.buxuesong.account.infrastructure.general.utils;

public class NumberUtils {
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
}
