package com.wh.sharding.utils;

import com.google.protobuf.ServiceException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Author: zhangxq
 * @CreateTime: 2023-11-20  10:09
 * @Description: 日期工具类
 * @Version: 1.0
 */
public class DateTools {

    /**
     * @description: 将日期字符串转为LocalDateTime
     * @author: zhangxq
     * @date: 2023/11/20 10:20
     * @param dateStr
     * @return: java.time.LocalDateTime
     **/
    public static LocalDateTime convertDateStrToLocalDateTime(String dateStr) throws ServiceException {
        return convertToLocalDateTime(convertToDate(dateStr));
    }

    /**
     * @description: 将date转为LocalDateTime
     * @author: zhangxq
     * @date: 2023/11/20 10:12
     * @param date
     * @return: java.time.LocalDateTime
     **/
    public static LocalDateTime convertToLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * @description: 将日期字符串转为Date
     * @author: zhangxq
     * @date: 2023/11/20 10:17
     * @param dateStr
     * @return: java.util.Date
     **/
    public static Date convertToDate(String dateStr) throws ServiceException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            throw new ServiceException("日期解析失败!");
        }
        return date;
    }

}
