package com.pharbers.kafka.connect.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/05/06 14:29
 */
public class CodeUtil {
    public static String md5Encode(String s){
        MessageDigest md5Ins = null;
        try {
            md5Ins = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Error("无此md5算法");
        }
        byte[] bytes = md5Ins.digest(s.getBytes());
        StringBuilder md5code = new StringBuilder();
        for (byte aByte : bytes) {
            md5code.append(Integer.toHexString((0x000000FF & aByte) | 0xFFFFFF00).substring(6));
        }
        return md5code.toString();
    }

    public static String base64Encode(String s){
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
}
