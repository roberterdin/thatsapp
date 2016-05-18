package com.whatistics.backend.dal;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * @author robert
 */
@Singleton
public class DataStoreProvider implements Provider<Datastore> {

    private final Morphia morphia = new Morphia();

    private Datastore ds;

    @Inject
    public DataStoreProvider(@Named("mongoClientHostname") String hostname, @Named("dbName") String database ){
        ds = morphia.createDatastore(new MongoClient(hostname), database);
    }

    @Override
    public Datastore get() {
        return ds;
    }
}
