package com.buxuesong.account.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {

    public static boolean isTradingTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime openTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(9).plusMinutes(15);
        LocalDateTime closeTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(15).plusMinutes(30);
        if (currentDateTime.compareTo(openTime) >= 0 && currentDateTime.compareTo(closeTime) <= 0) {
            return true;
        }
        return false;
    }
}
