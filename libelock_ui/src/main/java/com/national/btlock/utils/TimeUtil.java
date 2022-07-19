package com.national.btlock.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    static long startTime = 0;

    public static void timeSpanStart() {
        startTime = System.currentTimeMillis();
    }

    public static long timeSpanEnd() {
        return System.currentTimeMillis() - startTime;
    }


    public static String date2Str(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;

    }

    public static String datetime2Str(Date date) {
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm, Locale.CHINA");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String str = sdf.format(date);
        return str;

    }
    public static String datetime2Str2(Date date) {
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm, Locale.CHINA");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String str = sdf.format(date);
        return str;

    }

    public static int compareDate(String date1, String date2) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
//			Date dt2 = df.parse("2018-03-31 23:59");

            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
//                System.out.println("dt1 > dt2");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
//                System.out.println("dt1 < dt2");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    public static Date str2Date(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    public static Date datePlus(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static Date datePlus(String base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(str2Date(base));
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }


    public static int diffDays(String date1, String date2) {
        int result = 0;
        long diff = str2Date(date1).getTime() - str2Date(date2).getTime();

        result = (int) (diff / (24 * 60 * 60 * 1000));
        return result;

    }





}
