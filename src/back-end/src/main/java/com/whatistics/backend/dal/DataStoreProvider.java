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
    public DataStoreProvider(@Named("hostname") String hostname, @Named("database") String database ){
        morphia.mapPackage("com.whatistics.backend.model");
        ds = morphia.createDatastore(new MongoClient(hostname), database);
    }

    @Override
    public Datastore get() {
        return ds;
    }
}
