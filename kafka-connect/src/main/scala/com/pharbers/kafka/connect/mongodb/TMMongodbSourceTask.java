package com.pharbers.kafka.connect.mongodb;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.pharbers.kafka.connect.InputConfigKeys;
import com.pharbers.kafka.connect.mongodb.filter.Aggregation;
import com.pharbers.kafka.connect.oss.OssSourceTask;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.pharbers.kafka.connect.mongodb.TMMongodbSourceConnector.*;
import static com.mongodb.client.model.Filters.*;
/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/08/13 10:38
 */
public class TMMongodbSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(OssSourceTask.class);
    private static final String JOBID_FIELD = "iobId";
    private static final String POSITION_FIELD = "position";
    private static final String COLLECTION_NAME = "col";

    //外部参数
    private String jobId;
    private String topic;
    private int batchSize = TMMongodbSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private String connection;
    private String databaseName;
    private String periodsId;
    private String projectsId;
    private String proposalsId;
    //todo: 之后应该由配置传入，现在默认不要_id和job
    private List<String> blackCols = Stream.of("_id").collect(Collectors.toList());

    //线程共享变量，应该只赋值一次
    private MongoCursor<Document> documents;
    private Schema VALUE_SCHEMA;
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
    private String colJob;


    //线程共享会多次赋值变量
    private Long streamOffset;


    @Override
    public String version() {
        return new TMMongodbSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        jobId = props.get(InputConfigKeys.JOB_CONFIG);
        connection = props.get(InputConfigKeys.CONNECTION_CONFIG);
        topic = props.get(InputConfigKeys.TOPIC_CONFIG);
        databaseName = props.get(InputConfigKeys.DATABASE_CONFIG);
        //写死了
//        collectionName = props.get(InputConfigKeys.COLLECTION_CONFIG);
        batchSize = Integer.parseInt(props.get(InputConfigKeys.TASK_BATCH_SIZE_CONFIG));
        periodsId = props.get(PERIOD_COLL_NAME);
        projectsId = props.get(PROJECT_COLL_NAME);
        proposalsId = props.get(PROPOSAL_COLL_NAME);
        colJob = Aggregation.TmInputAgg(proposalsId, projectsId, periodsId);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException  {
        synchronized (this){
            if(documents == null){
                MongoCollection<BsonDocument> collection = MongoClients.create(connection).getDatabase(databaseName).getCollection(COLLECTION_NAME, BsonDocument.class);
                try {
                    setValueSchema(Objects.requireNonNull(collection.find(eq("job", colJob)).first()));
                } catch (Exception e) {
                    throw new ConnectException(e);
                }
                documents = MongoClients.create(connection).getDatabase(databaseName).getCollection(COLLECTION_NAME).find(eq("job", colJob)).iterator();
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
            Document document = documents.next();

            Struct value = new Struct(VALUE_SCHEMA);
            for (String s : document.keySet()) {
                if(blackCols.contains(s)){
                    continue;
                }
                value.put(s, document.get(s).toString());
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
        for(String s: document.keySet()){
            Schema fieldSchema;
            if(blackCols.contains(s)){
                continue;
            }
            switch (document.get(s).getBsonType()){
                case DOCUMENT: throw new Exception("不能解析struct嵌套");
                    //todo: 暂时这样
                case ARRAY: fieldSchema = Schema.STRING_SCHEMA;
                    break;
                default:  fieldSchema = Schema.STRING_SCHEMA;
            }
            schemaBuilder.field(s, fieldSchema);
        }
        VALUE_SCHEMA = schemaBuilder.build();
    }
}
