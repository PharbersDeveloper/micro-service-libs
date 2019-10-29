package com.pharbers.kafka.connect.oss.handler;

import jdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/10/29 10:28
 */
public class OffsetHandler {
    private Map<String, Long> streamOffset = new HashMap<>();
    public OffsetHandler(Map<String, Object> offset){
        for (Map.Entry<String, Object> kv : offset.entrySet()){
            streamOffset.put(kv.getKey(), Long.parseLong(kv.getValue().toString()));
        }
    }

    public Map<String, String> offsetKey(String filename) {
        return Collections.singletonMap("traceID", filename);
    }

    public Map<String, String> offsetValueCoding(){
        Map<String, String> res = new HashMap<>();
        for (Map.Entry<String, Long> kv : streamOffset.entrySet()){
            res.put(kv.getKey(), kv.getValue().toString());
        }
        return res;
    }

    public void add(String jobId){
        streamOffset.put(jobId, streamOffset.getOrDefault(jobId, 0L) + 1);
    }

    public long get(String jobId){
        return streamOffset.getOrDefault(jobId, 0L);
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, Long>kv : streamOffset.entrySet()){
            res.append(kv.getKey()).append(":").append(kv.getValue()).append("\n");
        }
        return res.toString();
    }
}
