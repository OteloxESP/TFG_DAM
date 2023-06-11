package com.example.oteloxtfgdam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oteloxtfgdam.R;
import com.example.oteloxtfgdam.db.DbManager;
import com.example.oteloxtfgdam.db.UsuariosDB;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    MongoCollection<UsuariosDB> mongoCollection;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameEditText = findViewById(R.id.username_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        mUsernameTextInputLayout = findViewById(R.id.username_text_input_layout);
        mPasswordTextInputLayout = findViewById(R.id.password_text_input_layout);
        mEmailTextInputLayout = findViewById(R.id.email_text_input_layout);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                boolean isValid = true;

                if (TextUtils.isEmpty(username)) {
                    mUsernameTextInputLayout.setError(getString(R.string.username_required_error));
                    isValid = false;
                }else{
                    mUsernameTextInputLayout.setError(null);
                }

                if (TextUtils.isEmpty(email)) {
                    mEmailTextInputLayout.setError(getString(R.string.email_required_error));
                    isValid = false;
                }else{
                    mEmailTextInputLayout.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordTextInputLayout.setError(getString(R.string.password_required_error));
                    isValid = false;
                }else{
                    mPasswordTextInputLayout.setError(null);
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailTextInputLayout.setError(getString(R.string.invalid_email));
                    isValid = false;
                }else{
                    mEmailTextInputLayout.setError(null);
                }
                if (isValid) {
                    String usuario = mUsernameEditText.getText().toString();
                    String contraseña = mPasswordEditText.getText().toString();
                    initializeMongoDB(usuario, contraseña, email);
                }
            }

            private void initializeMongoDB(String usuario, String contraseña, String email) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Cargando...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                DbManager db = new DbManager();
                mongoCollection = db.obtenerUsuariosCollection();
                RealmResultTask<MongoCursor<UsuariosDB>> findTask = mongoCollection.find().iterator();
                findTask.getAsync(task -> {
                    try {
                        Boolean v = true;
                        Boolean v2 = true;
                        if (task.isSuccess()) {
                            MongoCursor<UsuariosDB> results = task.get();
                            while (results.hasNext()) {
                                UsuariosDB u = results.next();
                                if (usuario.equals(u.getUsuario())){
                                    v = false;
                                }
                                if (email.equals(u.getEmail())){
                                    v2 = false;
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
                            showMensaje("Error","Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                        }

                        if (v && v2) {
                            mUsernameEditText.setError(null);
                            mEmailEditText.setError(null);
                            String salt = BCrypt.gensalt();
                            String contraseñaEncriptada = BCrypt.hashpw(contraseña, salt);
                            UsuariosDB nuevoUser = new UsuariosDB(
                                    new ObjectId(),
                                    usuario,
                                    contraseñaEncriptada,
                                    email
                            );

                            mongoCollection.insertOne(nuevoUser).getAsync(task2 -> {
                                if (task2.isSuccess()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Usuario nuevo registrado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    progressDialog.dismiss();
                                    Log.e("EXAMPLE", "failed to insert documents with: " + task2.getError().getErrorMessage());
                                    showMensaje("Error","Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
                                }
                            });
                        }else{
                            progressDialog.dismiss();
                            if (!v){
                                mUsernameTextInputLayout.setError(getString(R.string.usuario_ya_existe));
                            }
                            if (!v2){
                                mEmailTextInputLayout.setError(getString(R.string.email_ya_existe));
                            }
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("EXAMPLE", "Error during find task: ", e);
                        showMensaje("Error","Error al procesar los datos. Por favor, inténtalo de nuevo más tarde.");
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
