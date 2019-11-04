package com.pharbers.kafka.connect.mongodb;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.pharbers.kafka.connect.InputConfigKeys;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.bson.BsonDocument;
import org.bson.Document;
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
    private static final Logger log = LoggerFactory.getLogger(MongodbSourceTask.class);
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
    //todo: 之后应该由配置传入，现在默认不要_id
    private List<String> blackCols = new ArrayList<>();

    //线程共享变量，应该只赋值一次
    private MongoCursor<Document> documents;
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
        filter = props.get(InputConfigKeys.FILTER_CONFIG);
        blackCols.add("_id");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        synchronized (this) {
            if (documents == null) {
                MongoCollection<BsonDocument> collection = MongoClients.create(connection).getDatabase(databaseName).getCollection(collectionName, BsonDocument.class);
                try {
                    setValueSchema(Objects.requireNonNull(collection.find(Document.parse(filter)).first()));
                }
                catch (NullPointerException e) {
                    //todo: 不应该用这种异常来控制流程
                    log.info("查询结果为空");
                }
                catch (Exception e) {
                    throw new ConnectException(e);
                }
                documents = MongoClients.create(connection).getDatabase(databaseName).getCollection(collectionName).find(Document.parse(filter)).iterator();
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
                while (documents.hasNext() && docOffset > 0) {
                    documents.next();
                    docOffset--;
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
            Document document = documents.next();

            Struct value = new Struct(VALUE_SCHEMA);
            for (String s : document.keySet()) {
                if (blackCols.contains(s)) {
                    continue;
                }
                value.put(s, document.get(s) == null ? "" : document.get(s).toString());
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
        for (String s : document.keySet()) {
            Schema fieldSchema;
            if (blackCols.contains(s)) {
                continue;
            }
            switch (document.get(s).getBsonType()) {
                case DOCUMENT:
                    throw new Exception("不能解析struct嵌套");
                //todo: 暂时这样
                case ARRAY:
                    fieldSchema = Schema.STRING_SCHEMA;
                    break;
                default:
                    fieldSchema = Schema.STRING_SCHEMA;
            }
            schemaBuilder.field(s, fieldSchema);
        }
        VALUE_SCHEMA = schemaBuilder.build();
    }
}
