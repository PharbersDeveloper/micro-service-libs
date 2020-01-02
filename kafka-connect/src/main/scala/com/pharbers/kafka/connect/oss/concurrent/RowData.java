package com.pharbers.kafka.connect.oss.concurrent;

import com.redis.S;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/25 15:14
 */
public class RowData {
    private String type;
    private String[] row;
    private Map<String, Object> metaDate;
    private String jobId;
    private String traceId;


    public RowData(String type, String[] row, Map<String, Object> metaDate, String jobId, String traceId) {
        this.type = type;
        this.row = row;
        this.metaDate = metaDate;
        this.jobId = jobId;
        this.traceId = traceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getRow() {
        return row;
    }

    public void setRow(String[] row) {
        this.row = row;
    }

    public Map<String, Object> getMetaDate() {
        return metaDate;
    }

    public void setMetaDate(Map<String, Object> metaDate) {
        this.metaDate = metaDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
