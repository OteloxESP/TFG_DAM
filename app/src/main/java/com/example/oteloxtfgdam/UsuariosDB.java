package com.example.oteloxtfgdam;

import org.bson.types.ObjectId;

public class UsuariosDB {
    private ObjectId id;
    private String usuario;
    private String contraseña;

    private String email;

    public UsuariosDB() {

    }

    public UsuariosDB(ObjectId id, String usuario, String contraseña) {
        this.id = id;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public UsuariosDB(ObjectId id, String usuario, String contraseña, String email){
        this.id = id;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
