package com.pharbers.kafka.connect.mongodb;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.pharbers.kafka.connect.InputConfigKeys;
import com.pharbers.kafka.connect.oss.OssSourceTask;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/08/05 15:10
 */
public class MongodbSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(OssSourceTask.class);
    public static final String JOBID_FIELD = "iobId";
    public static final String POSITION_FIELD = "position";

    //外部参数
    private String jobId;
    private String topic;
    private int batchSize = MongodbSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private String connection;
    private String databaseName;
    private String collectionName;
    private String filter;

    //线程共享变量，应该只赋值一次
    private MongoCursor<BsonDocument> documents;
    private Schema VALUE_SCHEMA;
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;


    //线程共享会多次赋值变量
    private Long streamOffset;


    @Override
    public String version() {
        return new MongodbSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        jobId = props.get(InputConfigKeys.JOB_CONFIG);
        connection = props.get(InputConfigKeys.CONNECTION_CONFIG);
        topic = props.get(InputConfigKeys.TOPIC_CONFIG);
        databaseName = props.get(InputConfigKeys.DATABASE_CONFIG);
        collectionName = props.get(InputConfigKeys.COLLECTION_CONFIG);
        batchSize = Integer.parseInt(props.get(InputConfigKeys.TASK_BATCH_SIZE_CONFIG));
        filter = props.get(InputConfigKeys.FILE_CONFIG);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException  {
        synchronized (this){
            if(documents == null){
                MongoCollection<BsonDocument> collection = MongoClients.create(connection).getDatabase(databaseName).getCollection(collectionName, BsonDocument.class);
                //todo: 添加filter
                try {
                    setValueSchema(Objects.requireNonNull(collection.find().first()));
                } catch (Exception e) {
                    throw new ConnectException(e);
                }
                documents = collection.find().iterator();
                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(JOBID_FIELD, jobId));
                if (offset != null) {
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                        throw new ConnectException("Offset position is the incorrect type");
                    streamOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
                } else {
                    streamOffset = 0L;
                }
                log.info("offset:" + streamOffset);
                long docOffset = streamOffset;
                while (documents.hasNext() && docOffset > 0){
                    documents.next();
                    docOffset --;
                }
            }
        }
        ArrayList<SourceRecord> records = null;
        do {
            synchronized (this) {
                if (!documents.hasNext()) {
                    log.info("读取完成");
                    this.wait(1000);
                    break;
                }
            }
            BsonDocument document = documents.next();

            Struct value = new Struct(VALUE_SCHEMA);
            value.put("jobId", jobId);
            for (int i = 0; i < titleList.size(); i++) {
                String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
                value.put(titleList.get(i), v);
            }
            if (records == null)
                records = new ArrayList<>();
            synchronized (this) {
                streamOffset++;
            }
            records.add(new SourceRecord(offsetKey(jobId), offsetValue(streamOffset), topic, null,
                    KEY_SCHEMA, jobId, VALUE_SCHEMA, value, System.currentTimeMillis()));
        } while (records.size() < batchSize);
        return records;

        return null;
    }

    @Override
    public void stop() {
        documents.close();
    }

    private Map<String, String> offsetKey(String jobId) {
        return Collections.singletonMap(JOBID_FIELD, jobId);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }

    private void setValueSchema(BsonDocument document) throws Exception {
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();
        //todo: struct嵌套会出问题
        for(String s: document.keySet()){
            Schema fieldSchema;
            switch (document.get(s).getBsonType()){
                case STRING: fieldSchema = Schema.STRING_SCHEMA;
                break;
                case INT32: fieldSchema = Schema.INT32_SCHEMA;
                break;
                case INT64: fieldSchema = Schema.INT64_SCHEMA;
                break;
                default: {
                    log.error("未定义的类型", document.get(s).getBsonType());
                    throw new Exception("未定义的类型");
                }
            }
            schemaBuilder.field(s, fieldSchema);
        }
        VALUE_SCHEMA = schemaBuilder.build();
    }
}
