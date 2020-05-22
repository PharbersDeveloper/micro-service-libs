package com.pharbers.kafka.connect.oss;

import com.pharbers.kafka.connect.oss.concurrent.RowData;
import com.pharbers.kafka.connect.oss.concurrent.RowDataProducer;
import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import com.pharbers.kafka.connect.oss.model.Label;
import com.pharbers.kafka.connect.utils.FileTagUtil;
import com.pharbers.kafka.connect.utils.JsonUtil;
import com.pharbers.kafka.schema.OssTask;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cui
 * @ProjectName micro-service-libs
 * @ClassName OssCsvAndExcelSourceTask
 * @date 19-10-11 下午5:14
 * @Description: OssCsvAndExcelSourceTask
 */
public class OssCsvAndExcelSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(OssCsvAndExcelSourceTask.class);
    private ConsumerBuilder<String, OssTask> kafkaConsumerBuffer;
    private int batchSize;
    private TitleHandler titleHandler = new TitleHandler();
    /**
     * 暂时没啥用，所以可能会导致发不全的情况
     */
    private OffsetHandler offsetHandler = new OffsetHandler(new HashMap<>());
    private RecordBuilder recordBuilder;
    private LinkedBlockingQueue<RowData> plate;
    private ExecutorService executorService = null;
    private final String TAG_KEY = "_tag";

    @Override
    public String version() {
        return new OssCsvAndExcelSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        StringBuilder configs = new StringBuilder();
        props.forEach((k, v) -> configs.append(k).append(":").append(v).append("\n"));
        log.info("start:" + configs.toString());
        batchSize = Integer.parseInt(props.get(OssCsvAndExcelSourceConnector.TASK_BATCH_SIZE_CONFIG));
        plate = new LinkedBlockingQueue<>(batchSize * 8);
        recordBuilder = new RecordBuilder(props.get(OssCsvAndExcelSourceConnector.TOPIC_CONFIG));
        kafkaConsumerBuffer = new ConsumerBuilder<>(props.get(OssCsvAndExcelSourceConnector.OSS_TASK_TOPIC), OssTask.class);
        ThreadFactory threadFactory = new NameThreadFactory("kafka_listener");
        executorService = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory);
        executorService.execute(kafkaConsumerBuffer);
        RowDataProducer rowDataProducer = new RowDataProducer(kafkaConsumerBuffer, plate, props);
        executorService.execute(rowDataProducer);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> sources = new ArrayList<>();
        try {
            while (!plate.isEmpty() && sources.size() < batchSize){
                RowData value = plate.take();
                switch (value.getType()) {
                    case "SandBox-Schema": readTitle(value, sources);
                        break;
                    case "SandBox-Labels": readLabels(value, sources);
                        break;
                    case "SandBox": readData(value, sources);
                        break;
                    case "SandBox-Length": readLength(value, sources);
                        break;
                    default:
                }
            }
        } catch (InterruptedException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return  sources;
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            kafkaConsumerBuffer.close();
        } catch (NullPointerException e) {
            log.error("NullPointerException", e);
        }
    }

    private void readData(RowData row, List<SourceRecord> sources){
        List<String> titleList = titleHandler.getTitleMap().get(row.getJobId());
        String[] r = row.getRow();
        Map<String, String> rowValue = new HashMap<>(10);
        for (int i = 0; i < titleList.size(); i++) {
            String v = i >= r.length ? "" : r[i];
            rowValue.put(titleList.get(i), v);
        }
        OssTask task = (OssTask)row.getMetaDate().get("task");
        String sheetName = row.getMetaDate().get(row.getJobId()).toString();
        String tag = FileTagUtil.createTag(new String[]{task.getOwner().toString(), task.getFileName().toString(), sheetName, task.getCreateTime().toString()});
        rowValue.put(TAG_KEY, tag);
        addSources(sources, rowValue, row);
    }

    private void readTitle(RowData row, List<SourceRecord> sources){
        titleHandler.addTitle(row.getRow(), row.getJobId());
        List<ExcelTitle> title = titleHandler.getTitleForPoll(row.getJobId());
        title.add(new ExcelTitle(TAG_KEY, "String"));
        addSources(sources, title, row);
    }

    private void readLength(RowData row, List<SourceRecord> sources){
        Map<String, String> length = new HashMap<String, String>(1){{put("length", row.getRow()[0]);}};
        addSources(sources, length, row);
    }

    private void readLabels(RowData row, List<SourceRecord> sources){
        addSources(sources, new Label((OssTask)row.getMetaDate().get("task"), row.getRow()[0]), row);
    }

    private void addSources(List<SourceRecord> sources, Object data, RowData row){
        Struct value = new Struct(recordBuilder.valueSchema);
        value.put("jobId", row.getJobId());
        value.put("traceId", row.getTraceId());
        value.put("type", row.getType());
        try {
            value.put("data", JsonUtil.MAPPER.writeValueAsString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sources.add(recordBuilder.build(OffsetHandler.offsetKey(row.getJobId()), value, offsetHandler.offsetValueCoding()));
    }

    private Map<String, Object> getStreamOffset(String jobId) {
        String offsetKey = "jobId";
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(offsetKey, jobId));
        if (offset != null) {
            return offset;
        } else {
            return new HashMap<>(10);
        }
    }

    static class NameThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NameThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name +
                    POOL_NUMBER.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    class RecordBuilder{
        private String topic;
        private Schema keySchema;
        private Schema valueSchema;

        RecordBuilder(String topic){
            this.topic = topic;
            this.keySchema = SchemaBuilder.string().optional().build();
            this.valueSchema = SchemaBuilder.struct()
                    .field("jobId", Schema.STRING_SCHEMA)
                    .field("traceId", Schema.STRING_SCHEMA)
                    .field("type", Schema.STRING_SCHEMA)
                    .field("data", Schema.STRING_SCHEMA).build();
        }

        SourceRecord build(Map<String, String> sourceOffsetKey, Struct value, Map<String, ?> sourceOffset){
            return new SourceRecord(sourceOffsetKey, sourceOffset, topic, null,
                    keySchema, null, valueSchema, value, System.currentTimeMillis());
        }
    }

    protected LinkedBlockingQueue<RowData> getPlate() {
        return plate;
    }

    protected void setPlate(LinkedBlockingQueue<RowData> plate) {
        this.plate = plate;
    }

    protected int getBatchSize() {
        return batchSize;
    }

    protected void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public RecordBuilder getRecordBuilder() {
        return recordBuilder;
    }

    public void setRecordBuilder(RecordBuilder recordBuilder) {
        this.recordBuilder = recordBuilder;
    }
}
