package com.coderman.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间计算公式
 *
 * @author
 */
public class DateUtil {


    /**
     * 计算某个时间段之前的多少时间
     *
     * @param date 某一刻时间
     * @param many 之前多少天
     * @return
     */
    public static Date beforeCurrent(Date date, Integer many) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - many);
        return calendar.getTime();
    }

    /**
     * 当前时间向前推多少天
     *
     * @param many
     * @return
     */
    public static Date beforeCurrent(Integer many) {
        return beforeCurrent(new Date(System.currentTimeMillis()), many);
    }


    /**
     * 计算两个时间相差多少天
     *
     * @param minuendDate   被减时间
     * @param reductionDate 减去时间
     * @return
     */
    public static Long getDateCalculatedDay(Date minuendDate, Date reductionDate) {
        Calendar minuendCalendar = Calendar.getInstance();
        minuendCalendar.setTime(minuendDate);
        Calendar reductionCalendar = Calendar.getInstance();
        reductionCalendar.setTime(reductionDate);
        Long days = (minuendCalendar.getTime().getTime() - reductionCalendar.getTime().getTime()) / ConstantUtil.DAY_MS_NUM;
        return days;
    }

    /**
     * 计算当前时间距离多少天
     *
     * @param minuendDate
     * @return
     */
    public static Long getDateCalculatedDay(Date minuendDate) {
        return getDateCalculatedDay(minuendDate, new Date(System.currentTimeMillis()));
    }

    public static void main(String[] args) {
        Date date = beforeCurrent(30);
        System.out.println(getDateCalculatedDay(date, new Date(System.currentTimeMillis())));
    }

}
