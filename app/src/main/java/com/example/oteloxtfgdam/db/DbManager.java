package com.example.oteloxtfgdam.db;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.example.oteloxtfgdam.MyApp;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class DbManager {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    User user;
    App app;
    public DbManager(){
        app = MyApp.getAppInstance();
        if (app.currentUser() == null) {
            app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
                @Override
                public void onResult(App.Result<User> result) {
                    user = app.currentUser();
                }
            });
        }else{
            user = app.currentUser();
        }
    }

    public MongoCollection obtenerUsuariosCollection(){
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("bdoHelp");
        mongoCollection = mongoDatabase.getCollection("Usuarios");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<UsuariosDB> mongoCollection =
                mongoDatabase.getCollection(
                        "Usuarios",
                        UsuariosDB.class).withCodecRegistry(pojoCodecRegistry);
        return mongoCollection;
    }

    public MongoCollection obtenerZonasCollection(){
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("bdoHelp");
        mongoCollection = mongoDatabase.getCollection("Zonas");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<ZonasDB> mongoCollection =
                mongoDatabase.getCollection(
                        "Zonas",
                        ZonasDB.class).withCodecRegistry(pojoCodecRegistry);
        return mongoCollection;
    }

    public MongoCollection obtenerItemsCollection(){
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("bdoHelp");
        mongoCollection = mongoDatabase.getCollection("Items");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<ItemsDB> mongoCollection =
                mongoDatabase.getCollection(
                        "Items",
                        ItemsDB.class).withCodecRegistry(pojoCodecRegistry);
        return mongoCollection;
    }
}
