package com.liujian.gitbub.recyclerviewcitypickerdemo.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * 从数据库获取数据的帮助类
 * @author : liujian
 * @since : 2016/8/8 11:28
 */
public class PinyinUtils {
    /**
     * 获取拼音的首字母（大写）
     * @param pinyin String
     * @return String
     */
    public static String getFirstLetter(final String pinyin){
        if (TextUtils.isEmpty(pinyin)) return "定位";
        String c = pinyin.substring(0, 1);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c).matches()){
            return c.toUpperCase();
        } else if ("0".equals(c)){
            return "定位";
        } else if ("1".equals(c)){
            return "热门";
        }
        return "定位";
    }
}
