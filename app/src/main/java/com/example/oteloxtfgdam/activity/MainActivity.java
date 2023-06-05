package com.example.oteloxtfgdam.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.db.DbManager;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.google.android.material.textfield.TextInputLayout;

import org.mindrot.jbcrypt.BCrypt;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class MainActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
    MongoCollection<UsuariosDB> mongoCollection;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtén una instancia de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Obtén el estado de inicio de sesión desde SharedPreferences
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent loginIntent = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(loginIntent);
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
                    initializeMongoDB(usuario, contraseña);
                }
            }

            private void initializeMongoDB(String usuario, String contraseña) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Iniciando sesión...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                DbManager db = new DbManager();
                mongoCollection = db.obtenerUsuariosCollection();
                RealmResultTask<MongoCursor<UsuariosDB>> findTask = mongoCollection.find().iterator();
                findTask.getAsync(task -> {
                    try {
                        Boolean v = false;
                        Boolean v2 = false;
                        int tala = 0;
                        int carne = 0;
                        int hierbas = 0;
                        int sangre = 0;

                        if (task.isSuccess()) {
                            MongoCursor<UsuariosDB> results = task.get();
                            Log.v("EXAMPLE", "successfully found documents:");
                            while (results.hasNext()) {
                                UsuariosDB u = results.next();
                                if (usuario.equals(u.getUsuario())) {
                                    v = true;
                                    if (BCrypt.checkpw(contraseña, u.getContraseña())) {
                                        v2 = true;
                                        tala = u.getMaestriaTala();
                                        carne = u.getMaestriaCarne();
                                        hierbas = u.getMaestriaHierbas();
                                        sangre = u.getMaestriaSangre();
                                    }
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                            showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                        }

                        if (v && v2) {
                            mUsernameTextInputLayout.setError(null);
                            mPasswordTextInputLayout.setError(null);
                            // Guarda el estado de inicio de sesión
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("user", usuario);
                            editor.putString("password", contraseña);
                            editor.putInt("tala", tala);
                            editor.putInt("hierbas", hierbas);
                            editor.putInt("carne", carne);
                            editor.putInt("sangre", sangre);
                            editor.apply();
                            Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (!v || !v2) {
                            mUsernameTextInputLayout.setError(getString(R.string.username_incorrect));
                            mPasswordTextInputLayout.setError(getString(R.string.password_incorrect));

                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("EXAMPLE", "Error during find task: ", e);
                        showMensaje("Error", "Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                    }
                });
            }
        });

    }

    private void showMensaje(String titulo, String mensaje) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle(titulo)
                        .setMessage(mensaje)
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }
}

