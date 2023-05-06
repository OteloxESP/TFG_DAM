package com.example.oteloxtfgdam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencias a los componentes de la vista
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mUsernameTextInputLayout = findViewById(R.id.username_text_input_layout);
        mPasswordTextInputLayout = findViewById(R.id.password_text_input_layout);

        // Configurar un Listener para el botón de inicio de sesión
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
                    // Procesar el inicio de sesión
                    // ...
                }
            }
        });
    }
}
