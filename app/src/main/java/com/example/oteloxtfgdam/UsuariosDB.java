package com.example.oteloxtfgdam;

import org.bson.types.ObjectId;

public class UsuariosDB {
    private ObjectId id;
    private String usuario;
    private String contraseña;

    public UsuariosDB() {

    }

    public UsuariosDB(ObjectId id, String usuario, String contraseña) {
        this.id = id;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}
