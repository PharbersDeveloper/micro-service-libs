package com.pharbers.kafka.connect.oss.readerV2;

import com.pharbers.kafka.connect.oss.concurrent.RowData;
import com.pharbers.kafka.schema.OssTask;
import com.redis.S;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/25 16:22
 */
public class CsvReaderV2 implements ReaderV2 {
    private BufferedReader reader;
    private String jobId;
    private Map<String, Object> metaDate = new HashMap<>();
    private OssTask task;
    //todo: 和cvsReader统一

    private final int TITLE_MAX_INDEX = 100;
    private final String TITLE_TYPE = "SandBox-Schema";
    private final String LABELS_TYPE = "SandBox-Labels";
    private final String DATA_TYPE = "SandBox";
    private final String LENGTH_TYPE = "SandBox-Length";
    private final String DEFAULT_REGEX = ",";

    public CsvReaderV2(String jobIdPrefix, OssTask task) {
        this.jobId = jobIdPrefix + 0;
        this.task = task;
        metaDate.put("task", task);
    }

    @Override
    public void read(BlockingQueue<RowData> seq) throws Exception {
        String row = reader.readLine();
        List<String> cacheList = new ArrayList<>();
        for (int i = 0; i < TITLE_MAX_INDEX; i++) {
            if (row != null) {
                cacheList.add(row);
                row = reader.readLine();
            } else {
                break;
            }
        }
        List<String> titleBeginList = getBeginList(cacheList, seq, jobId);
        seq.put(new RowData(LABELS_TYPE, new String[]{""}, metaDate, jobId, task.getTraceId().toString()));
        Long cacheLength = putRow(seq, titleBeginList.iterator(), jobId, 0);
        Long length = putRow(seq, jobId, cacheLength);
        seq.put(new RowData(LENGTH_TYPE, new String[]{length.toString()}, metaDate, jobId, task.getTraceId().toString()));
        close();
    }

    @Override
    public void init(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
    }

    @Override
    public boolean isEnd() {
        return false;
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getBeginList(List<String> cacheList, BlockingQueue<RowData> seq, String jobId) throws InterruptedException {
        List<String> titleValues = new ArrayList<>();
        int titleIndex = 0;
        for(int i = 0; i < cacheList.size(); i++){
            List<String> values = new ArrayList<>();
            for(String cell : cacheList.get(i).split(DEFAULT_REGEX)){
                if(!"".equals(cell)){
                    values.add(cell);
                }
            }

            if(values.size() > titleValues.size()){
                titleValues = values;
                titleIndex = i;
            }
        }
        String[] schema = titleValues.toArray(new String[0]);
        seq.put(new RowData(TITLE_TYPE, schema, null, jobId, task.getTraceId().toString()));
        return cacheList.subList(titleIndex + 1, cacheList.size());
    }

    private Long putRow(BlockingQueue<RowData> seq, Iterator<String> rows, String jobId, long beginLength) throws InterruptedException {
        long length = beginLength;
        while (rows.hasNext()) {
            String row = rows.next();
            List<String> cellValues = new ArrayList<>(Arrays.asList(row.split(DEFAULT_REGEX)));
            if (cellValues.stream().allMatch(""::equals)) {
                continue;
            }
            seq.put(new RowData(DATA_TYPE, cellValues.toArray(new String[0]), null, jobId, task.getTraceId().toString()));
            length++;
        }
        return length;
    }

    private Long putRow(BlockingQueue<RowData> seq, String jobId, long beginLength) throws Exception {
        long length = beginLength;
        String row = reader.readLine();
        while (row != null){
            List<String> cellValues = new ArrayList<>(Arrays.asList(row.split(DEFAULT_REGEX)));
            row = reader.readLine();
            if (cellValues.stream().allMatch(""::equals)) {
                continue;
            }
            seq.put(new RowData(DATA_TYPE, cellValues.toArray(new String[0]), null, jobId, task.getTraceId().toString()));
            length ++;
        }
        return length;
    }
}
