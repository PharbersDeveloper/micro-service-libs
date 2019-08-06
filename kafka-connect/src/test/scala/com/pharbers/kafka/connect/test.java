package com.pharbers.kafka.connect;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import java.util.function.Consumer;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/08/05 13:50
 */
public class test {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.100.176:27017");
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<BsonDocument> collection = database.getCollection("PhProfileProp", BsonDocument.class);
        try (MongoCursor<BsonDocument> cursor = collection.find(eq("company_id", "5afa53bded925c05c6f69c54")).iterator()) {
            while (cursor.hasNext()) {
                BsonDocument document = cursor.next();
                for(String key: document.keySet()){
//                    System.out.println(document.getBsonType());
                    System.out.println(document.get(key).getBsonType());
                }
                System.out.println(document.toJson());
            }
        }
        Consumer<BsonDocument> printBlock = document -> System.out.println(document.toJson());
        collection.find(eq("company_id", "5afa53bded925c05c6f69c54")).forEach(printBlock);
        MongoCursor<Document> cursor2 = database.getCollection("PhProfileProp").find( Document.parse("{'company_id':'5afa53bded925c05c6f69c54'}")).iterator();
        Document document = cursor2.next();
        for (String s : document.keySet()) {
            Object obj =  document.get(s);
            System.out.println(obj);
        }
    }

}
