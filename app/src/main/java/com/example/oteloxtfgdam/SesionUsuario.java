package com.example.oteloxtfgdam;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionUsuario {

    private static SesionUsuario instance;
    private static Context context;

    private static final String NOMBRE_SESION = "Sesion";
    private static final String CLAVE_NOMBRE_USUARIO = "usuario";

    private SesionUsuario(Context context) {
        this.context = context;
    }

    public static synchronized SesionUsuario getInstance(Context context) {
        if (instance == null) {
            instance = new SesionUsuario(context);
        }
        return instance;
    }

    public void setUsuario(String nombreUsuario) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOMBRE_SESION, Context.MODE_PRIVATE).edit();
        editor.putString(CLAVE_NOMBRE_USUARIO, nombreUsuario);
        editor.apply();
    }

    public String getUsuario() {
        SharedPreferences preferences = context.getSharedPreferences(NOMBRE_SESION, Context.MODE_PRIVATE);
        return preferences.getString(CLAVE_NOMBRE_USUARIO, null);
    }

    public void borrarSesion() {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOMBRE_SESION, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public boolean estaLogueado() {
        SharedPreferences preferences = context.getSharedPreferences(NOMBRE_SESION, Context.MODE_PRIVATE);
        return preferences.getString(CLAVE_NOMBRE_USUARIO, null) != null;
    }
}

