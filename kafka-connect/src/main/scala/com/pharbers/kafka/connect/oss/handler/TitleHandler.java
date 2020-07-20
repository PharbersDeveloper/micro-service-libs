package com.pharbers.kafka.connect.oss.handler;

import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/10/28 18:50
 */
public class TitleHandler {

    private Map<String, List<String>> titleMap = new HashMap<>();
    private Map<String, List<ExcelTitle>> title = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private List<String> jobIdList = new LinkedList<>();
    private final int MAX_TITLE_NUM = 100;

    public TitleHandler(String[] titleRow, String jobId) {
        addTitle(titleRow, jobId);
    }

    public TitleHandler() {
    }

    public void addTitle(String[] titleRow, String jobId) {
        List<ExcelTitle> sheetTitle = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        int titleIndex = 0;
        for (String value : titleRow) {
            if (!"".equals(value)) {
                titleList.add(titleIndex + "#" + value);
                sheetTitle.add(new ExcelTitle(titleIndex + "#" + value, "String"));
            }
            titleIndex ++;
        }
        titleMap.put(jobId, titleList);
        title.put(jobId, sheetTitle);
        jobIdList.add(jobId);
        if(jobIdList.size() > MAX_TITLE_NUM){
            titleMap.remove(jobIdList.get(0));
            title.remove(jobIdList.get(0));
            jobIdList.remove(0);
        }
    }

    public Struct titleBuild(Schema schema, String traceId, String jobId) {
        Struct headValue = new Struct(schema);
        headValue.put("jobId", jobId);
        headValue.put("traceId", traceId);
        headValue.put("type", "SandBox-Schema");
        try {
            headValue.put("data", mapper.writeValueAsString(title.get(jobId)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headValue;
    }

    public Map<String, List<String>> getTitleMap() {
        return titleMap;
    }

    public List<ExcelTitle> getTitleForPoll(String jobId) {
        return title.getOrDefault(jobId, new ArrayList<>());
    }

    public void resetTitle(String[] titleRow, String jobId, String oldJobId){
        titleMap.remove(oldJobId);
        title.remove(oldJobId);
        addTitle(titleRow, jobId);
    }
}
