package com.example.oteloxtfgdam.db;

import org.bson.types.ObjectId;

public class UsuariosDB {
    private ObjectId id;
    private String usuario;
    private String contraseña;
    private String email;
    private int maestria_tala = 1000;
    private int maestria_sangre = 1000;
    private int maestria_hierbas = 1000;
    private int maestria_carne = 1000;
    private int maestria_minerales = 1000;
    private int maestria_curtir = 1000;

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
        this.maestria_tala = 1000;
        this.maestria_sangre = 1000;
        this.maestria_hierbas = 1000;
        this.maestria_carne = 1000;
        this.maestria_minerales = 1000;
        this.maestria_curtir = 1000;
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

    public int getMaestria_tala() {
        return maestria_tala;
    }

    public void setMaestria_tala(int maestria_tala) {
        this.maestria_tala = maestria_tala;
    }

    public int getMaestria_sangre() {
        return maestria_sangre;
    }

    public void setMaestria_sangre(int maestria_sangre) {
        this.maestria_sangre = maestria_sangre;
    }

    public int getMaestria_hierbas() {
        return maestria_hierbas;
    }

    public void setMaestria_hierbas(int maestria_hierbas) {
        this.maestria_hierbas = maestria_hierbas;
    }

    public int getMaestria_carne() {
        return maestria_carne;
    }

    public void setMaestria_carne(int maestria_carne) {
        this.maestria_carne = maestria_carne;
    }

    public int getMaestria_minerales() {
        return maestria_minerales;
    }

    public void setMaestria_minerales(int maestria_minerales) {
        this.maestria_minerales = maestria_minerales;
    }

    public int getMaestria_curtir() {
        return maestria_curtir;
    }

    public void setMaestria_curtir(int maestria_curtir) {
        this.maestria_curtir = maestria_curtir;
    }
}
