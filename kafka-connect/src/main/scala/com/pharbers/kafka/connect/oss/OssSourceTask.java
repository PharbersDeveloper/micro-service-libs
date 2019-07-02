package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jeorch
 * @ProjectName micro-service-libs
 * @ClassName OssSourceTask
 * @date 19-7-1下午7:46
 * @Description: TODO
 */
public class OssSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(OssSourceTask.class);
    public static final String FILENAME_FIELD = "ossKey";
    public  static final String POSITION_FIELD = "position";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String ossKey;
    private InputStream stream;
    private BufferedReader reader = null;
    private String topic = null;
    private int batchSize = OssSourceConnector.DEFAULT_TASK_BATCH_SIZE;

    private Long streamOffset;

    @Override
    public String version() {
        return new OssSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        endpoint = props.get(OssSourceConnector.ENDPOINT_CONFIG);
        accessKeyId = props.get(OssSourceConnector.ACCESS_KEY_ID_CONFIG);
        accessKeySecret = props.get(OssSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        bucketName = props.get(OssSourceConnector.BUCKET_NAME_CONFIG);
        ossKey = props.get(OssSourceConnector.KEY_CONFIG);
        if (ossKey == null || ossKey.isEmpty()) {
            stream = System.in;
            // Tracking offset for stdin doesn't make sense
            streamOffset = null;
        }
        // Missing topic or parsing error is not possible because we've parsed the config in the
        // Connector
        topic = props.get(OssSourceConnector.TOPIC_CONFIG);
        batchSize = Integer.parseInt(props.get(OssSourceConnector.TASK_BATCH_SIZE_CONFIG));
    }

    @Override
    public List<SourceRecord> poll() {

        OSS client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        if (stream == null) {
            try {
                log.info("Polling object from oss");
                OSSObject object = client.getObject(bucketName, ossKey);
                log.info("Contest-Type: " + object.getObjectMetadata().getContentType());
                stream = object.getObjectContent();
                streamOffset = 0L;
                reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                ArrayList<SourceRecord> records = null;

                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        log.trace("Read a line from {}", logFilename());
                        if (records == null)
                            records = new ArrayList<>();
                        streamOffset += (line.length() + 1);
                        records.add(new SourceRecord(offsetKey(ossKey), offsetValue(streamOffset), topic, null,
                                null, null, VALUE_SCHEMA, line, System.currentTimeMillis()));

                        if (records.size() >= batchSize) {
                            return records;
                        }
                    }
                } while (line != null);

                reader.close();
                log.info("records over");
                return records;

            } catch (IOException e) {
                // Underlying stream was killed, probably as a result of calling stop. Allow to return
                // null, and driving thread will handle any shutdown if necessary.
            } catch (OSSException oe) {
                System.out.println("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                System.out.println("Error Message: " + oe.getErrorCode());
                System.out.println("Error Code:       " + oe.getErrorCode());
                System.out.println("Request ID:      " + oe.getRequestId());
                System.out.println("Host ID:           " + oe.getHostId());
            } catch (ClientException ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message: " + ce.getMessage());
            } finally {
                /*
                 * Do not forget to shut down the client finally to release all allocated resources.
                 */
                client.shutdown();
            }
        }
        return null;
    }

    @Override
    public void stop() {
        log.info("Stopping");
        synchronized (this) {
            try {
                stream.close();
                log.info("Closed input stream");
            } catch (IOException e) {
                log.error("Failed to close FileStreamSourceTask stream: ", e);
            }
            this.notify();
        }
    }

    private Map<String, String> offsetKey(String filename) {
        return Collections.singletonMap(FILENAME_FIELD, filename);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }

    private String logFilename() {
        return ossKey == null ? "stdin" : ossKey;
    }

}
