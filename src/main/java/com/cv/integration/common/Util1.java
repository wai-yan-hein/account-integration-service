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

    public static String isNull(String input, String output) {
        return isNullOrEmpty(input) ? output : input;
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return date != null ? formatter.format(date) : null;
    }

    private static String cusAcc;
    private static String supAcc;
    private static boolean multiCur;

    public static String getCusAcc() {
        return cusAcc;
    }

    public static void setCusAcc(String cusAcc) {
        Util1.cusAcc = cusAcc;
    }

    public static String getSupAcc() {
        return supAcc;
    }

    public static void setSupAcc(String supAcc) {
        Util1.supAcc = supAcc;
    }

    public static boolean isMultiCur() {
        return multiCur;
    }

    public static void setMultiCur(boolean multiCur) {
        Util1.multiCur = multiCur;
    }

    public static boolean getBoolean(String obj) {
        boolean status = false;
        if (!Objects.isNull(obj)) {
            status = obj.equals("1") || obj.toLowerCase().equals("true");
        }
        return status;

    }


    public static double getDouble(Object obj) {
        return obj == null ? 0 : Double.parseDouble(obj.toString());
    }
}
