package com.pharbers.kafka.connect.oss.model;

import java.util.List;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/05/19 15:38
 */
public class BloodMsg {
    private String assetId;
    private java.util.List<String> parentIds;
    private String jobId;
    private java.util.List<String> colName;
    private String status;
    private String description;

    public BloodMsg(String assetId, List<String> parentIds, String jobId, List<String> colName, String status, String description) {
        this.assetId = assetId;
        this.parentIds = parentIds;
        this.jobId = jobId;
        this.colName = colName;
        this.status = status;
        this.description = description;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<String> getColName() {
        return colName;
    }

    public void setColName(List<String> colName) {
        this.colName = colName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
