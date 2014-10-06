package mongodb;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;


public class MongoDB {

	private DB db;
	private DBCollection collection;
	private MongoClient mongoClient;
	public MongoDB(String dbName) {
		try {
			mongoClient = new MongoClient();
			db = mongoClient.getDB(dbName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DBCollection getCollection(String name) {
		if (db.collectionExists("message")) {
			collection = db.getCollection("message");
		} else {
			DBObject options = BasicDBObjectBuilder.start().add("size", 214748369).get();
			collection = db.createCollection("message", options);
		}
		return collection;
	}

	public void insertDoc(String collectionName, DBObject doc) {
		this.getCollection(collectionName).insert(doc);
	}

	public void dropCollection(DBCollection collection) {
		collection.drop();
	}
	public void close(){
		db.dropDatabase();
        mongoClient.close();
	}
}
