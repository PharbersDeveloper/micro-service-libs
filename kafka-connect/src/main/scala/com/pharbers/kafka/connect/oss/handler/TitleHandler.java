package com.pharbers.kafka.connect.oss.handler;

import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

    public TitleHandler(String[] titleRow, String traceID) {
        addTitle(titleRow, traceID,0);
    }

    public TitleHandler() {
    }

    public void addTitle(String[] titleRow, String traceID, int index) {
        List<ExcelTitle> sheetTitle = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (String value : titleRow) {
            titleList.add(value);
            if (!value.equals("")) sheetTitle.add(new ExcelTitle(value, "String"));
        }
        titleMap.put(traceID + index, titleList);
        title.put(traceID + index, sheetTitle);
    }

    public Struct titleBuild(Schema schema, String traceID, String jobID) {
        Struct headValue = new Struct(schema);
        headValue.put("jobId", jobID);
        headValue.put("traceId", traceID);
        headValue.put("type", "SandBox-Schema");
        try {
            headValue.put("data", mapper.writeValueAsString(title.get(jobID)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headValue;
    }

    public Map<String, List<String>> getTitleMap() {
        return titleMap;
    }

    public Map<String, List<ExcelTitle>> getTitle() {
        return title;
    }
}
