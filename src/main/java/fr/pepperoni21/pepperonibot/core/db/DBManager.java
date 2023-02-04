package fr.pepperoni21.pepperonibot.core.db;

import com.mongodb.client.MongoClients;
import dev.morphia.Morphia;

import static fr.pepperoni21.pepperonibot.References.DOTENV;

public class DBManager {

    public DBManager(){
        this.connect();
    }

    public void connect(){
        String uri = DOTENV.get("MONGODB_URI");
        DBReferences.datastore = Morphia
                .createDatastore(MongoClients.create(uri), "db");
    }

}
