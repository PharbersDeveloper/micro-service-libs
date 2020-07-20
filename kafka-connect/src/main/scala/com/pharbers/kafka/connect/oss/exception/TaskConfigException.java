package com.pharbers.kafka.connect.oss.exception;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/06/08 15:43
 */
public class TaskConfigException extends Exception {
    public TaskConfigException(String msg){
        super(msg);
    }
}
