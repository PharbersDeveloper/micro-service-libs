package com.pharbers.kafka.connect.utils;

import java.util.List;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/05/07 11:03
 */
public class FileTagUtil {
    public static String TAG_PR = "phf";
    public static String createTag(String[] labels){
        StringBuilder res = new StringBuilder(TAG_PR);
        for(String label: labels){
            res.append((char)31).append(label);
        }
        return res.toString();
    }
}
