package com.whatistics.backend.dal;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.whatistics.backend.model.TestEntity;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * @author robert
 */
@Singleton
public class DataStoreProvider implements Provider<Datastore> {

    private final Morphia morphia = new Morphia();
    private Datastore ds;
    private String hostname;
    private String database;

    @Inject
    public DataStoreProvider(@Named("mongoClientHostname") String hostname, @Named("dbName") String database ){
        this.hostname = hostname;
        this.database = database;
    }

    @Override
    public Datastore get() {
        if (ds == null){
            MongoClient mc = new MongoClient(hostname, MongoClientOptions.builder()
                    .writeConcern(WriteConcern.ACKNOWLEDGED)
                    .build());
            ds = morphia.createDatastore(mc, database);
        }
        return ds;
    }
}
