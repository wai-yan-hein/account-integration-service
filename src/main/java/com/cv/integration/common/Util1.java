package com.cv.integration.common;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
public class Util1 {
    public static HashMap<String, String> hmSysProp = new HashMap<>();

    public static Date getTodayDate() {
        return Calendar.getInstance().getTime();
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return date != null ? formatter.format(date) : null;
    }

    public static boolean getBoolean(String obj) {
        boolean status = false;
        if (!Objects.isNull(obj)) {
            status = obj.equals("1") || obj.toLowerCase().equals("true");
        }
        return status;

    }

    public static boolean isMultiCur() {
        return Util1.getBoolean(hmSysProp.get("system.multi.currency.flag"));
    }

    public static String getProperty(String key) {
        return hmSysProp.get(key);
    }
}
