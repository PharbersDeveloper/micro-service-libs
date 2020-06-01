package com.pharbers.kafka.connect.oss.model;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/06/01 11:23
 */
public class TypeErrorMsg {
    private String traceId;
    private String jobId;
    private String type;

    public TypeErrorMsg(String traceId, String jobId, String type) {
        this.traceId = traceId;
        this.jobId = jobId;
        this.type = type;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
