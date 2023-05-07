package com.example.oteloxtfgdam;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    Realm uiThreadRealm;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    User user;
    App app;
    String AppId = "bdoinfo-wwrmh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        // Set up click listener for register button
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String username = mUsernameEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                boolean isValid = true;
                // Validate inputs
                if (TextUtils.isEmpty(username)) {
                    mUsernameEditText.setError(getString(R.string.username_required_error));
                    isValid = false;
                }else{
                    mUsernameEditText.setError(null);
                }

                if (TextUtils.isEmpty(email)) {
                    mEmailEditText.setError(getString(R.string.email_required_error));
                    isValid = false;
                }else{
                    mEmailEditText.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordEditText.setError(getString(R.string.password_required_error));
                    isValid = false;
                }else{
                    mPasswordEditText.setError(null);
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEditText.setError(getString(R.string.invalid_email));
                    isValid = false;
                }else{
                    mEmailEditText.setError(null);
                }
                if (isValid) {
                    String usuario = mUsernameEditText.getText().toString();
                    String contraseña = mPasswordEditText.getText().toString();
                    Realm.init(v.getContext());
                    String appID = "bdoinfo-wwrmh";
                    app = new App(new AppConfiguration.Builder(AppId).build());
                    if (app.currentUser() == null) {
                        Toast.makeText(v.getContext(), "user is null", Toast.LENGTH_SHORT).show();
                        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
                            @Override
                            public void onResult(App.Result<User> result) {
                                if (result.isSuccess()) {
                                    Log.v("User", "Logged In Successfully");
                                    initializeMongoDB(usuario, contraseña, email);
                                    //Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.v("User", "Failed to Login");
                                    Toast.makeText(v.getContext(), "Failed to login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        //Toast.makeText(v.getContext(), "user is not null", Toast.LENGTH_SHORT).show();
                        initializeMongoDB(usuario, contraseña, email);
                    }
                }else{

                }
                // Show success message and finish activity
                Toast.makeText(v.getContext(), R.string.registration_success, Toast.LENGTH_SHORT).show();
            }

            private void initializeMongoDB(String usuario, String contraseña, String email) {
                user = app.currentUser();
                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("bdoHelp");
                mongoCollection = mongoDatabase.getCollection("Usuarios");
                CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
                MongoCollection<UsuariosDB> mongoCollection =
                        mongoDatabase.getCollection(
                                "Usuarios",
                                UsuariosDB.class).withCodecRegistry(pojoCodecRegistry);
                Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle");
                RealmResultTask<MongoCursor<UsuariosDB>> findTask = mongoCollection.find().iterator();
                findTask.getAsync(task -> {
                    try {
                        Boolean v = false;
                        Boolean v2 = false;
                        if (task.isSuccess()) {
                            MongoCursor<UsuariosDB> results = task.get();
                            Log.v("EXAMPLE", "successfully found documents:");
                            while (results.hasNext()) {
                                UsuariosDB u = results.next();
                                if (usuario.equals(u.getUsuario())){
                                    v = true;
                                    if (email.equals(u.getEmail())){
                                        v2 = true;
                                    }
                                }
                            }
                        } else {
                            Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                        }

                        if (!v && !v2)
                        {
                            UsuariosDB nuevoUser = new UsuariosDB(
                                    new ObjectId(),
                                    usuario,
                                    contraseña,
                                    email
                            );

                            mongoCollection.insertOne(nuevoUser).getAsync(task2 -> {
                                if (task2.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Usuario nuevo registrado", Toast.LENGTH_SHORT).show();
                                    Log.v("EXAMPLE", "successfully inserted a document with id: " + task2.get().getInsertedId());
                                } else {
                                    Log.e("EXAMPLE", "failed to insert documents with: " + task2.getError().getErrorMessage());
                                }
                            });
                        }


                    } catch (Exception e) {
                        Log.e("EXAMPLE", "Error during find task: ", e);
                    }
                });
            }
        });

    }

}
