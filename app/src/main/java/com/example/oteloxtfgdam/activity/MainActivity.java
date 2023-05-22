package com.example.oteloxtfgdam.activity;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.SesionUsuario;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.mindrot.jbcrypt.BCrypt;

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

public class MainActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
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
        setContentView(R.layout.activity_main);

        if (SesionUsuario.getInstance(this).estaLogueado()) {
            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
            finish();
        }

        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mUsernameTextInputLayout = findViewById(R.id.username_text_input_layout);
        mPasswordTextInputLayout = findViewById(R.id.password_text_input_layout);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar los campos de entrada
                boolean isValid = true;
                if (TextUtils.isEmpty(mUsernameEditText.getText())) {
                    mUsernameTextInputLayout.setError(getString(R.string.username_required_error));
                    isValid = false;
                } else {
                    mUsernameTextInputLayout.setError(null);
                }
                if (TextUtils.isEmpty(mPasswordEditText.getText())) {
                    mPasswordTextInputLayout.setError(getString(R.string.password_required_error));
                    isValid = false;
                } else {
                    mPasswordTextInputLayout.setError(null);
                }

                // Si los campos son válidos, continuar con el proceso de inicio de sesión
                if (isValid) {
                    String usuario = mUsernameEditText.getText().toString();
                    String contraseña = mPasswordEditText.getText().toString();
                    Realm.init(v.getContext());
                    app = new App(new AppConfiguration.Builder(AppId).build());
                    if (app.currentUser() == null) {
                        Toast.makeText(v.getContext(), "user is null", Toast.LENGTH_SHORT).show();
                        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
                            @Override
                            public void onResult(App.Result<User> result) {
                                if (result.isSuccess()) {
                                    initializeMongoDB(usuario, contraseña);

                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        initializeMongoDB(usuario, contraseña);
                    }
                }
            }
            private void initializeMongoDB(String usuario, String contraseña) {
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
                                    if (BCrypt.checkpw(contraseña, u.getContraseña())){
                                        v2 = true;
                                    }
                                }
                            }
                        } else {
                            Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                        }

                        if (v && v2){
                            mUsernameTextInputLayout.setError(null);
                            mPasswordTextInputLayout.setError(null);
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            SesionUsuario.getInstance(MainActivity.this).setUsuario(usuario);
                            Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                            intent.putExtra("app", (CharSequence) app);
                            startActivity(intent);
                            finish();
                        }
                        else if (!v && !v2)
                        {
                            mUsernameTextInputLayout.setError(getString(R.string.username_incorrect));
                            mPasswordTextInputLayout.setError(getString(R.string.password_incorrect));

                        } else if (v && !v2) {
                            mPasswordTextInputLayout.setError(getString(R.string.password_incorrect));
                        }

                    } catch (Exception e) {
                        Log.e("EXAMPLE", "Error during find task: ", e);
                    }
                });
            }
        });

    }
}
