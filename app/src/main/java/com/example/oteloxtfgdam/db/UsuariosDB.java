package com.example.oteloxtfgdam.db;

import org.bson.types.ObjectId;

public class UsuariosDB {
    private ObjectId id;
    private String usuario;
    private String contraseña;
    private String email;
    private int maestriaTala;
    private int maestriaSangre;
    private int maestriaHierbas;
    private int maestriaCarne;
    private byte[] imagen;
    private int administrador;

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
        this.maestriaTala = 1000;
        this.maestriaSangre = 1000;
        this.maestriaHierbas = 1000;
        this.maestriaCarne = 1000;
        this.imagen = new byte[0];
        this.administrador = 0;
    }

    public UsuariosDB(ObjectId id, String usuario, String contraseña, String email, int tala, int sangre, int hierbas, int carne, byte[] imagen){
        this.id = id;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.email = email;
        this.maestriaTala = tala;
        this.maestriaSangre = sangre;
        this.maestriaHierbas = hierbas;
        this.maestriaCarne = carne;
        this.imagen = imagen;
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

    public int getMaestriaTala() {
        return maestriaTala;
    }

    public void setMaestriaTala(int maestriaTala) {
        this.maestriaTala = maestriaTala;
    }

    public int getMaestriaSangre() {
        return maestriaSangre;
    }

    public void setMaestriaSangre(int maestriaSangre) {
        this.maestriaSangre = maestriaSangre;
    }

    public int getMaestriaHierbas() {
        return maestriaHierbas;
    }

    public void setMaestriaHierbas(int maestriaHierbas) {
        this.maestriaHierbas = maestriaHierbas;
    }

    public int getMaestriaCarne() {
        return maestriaCarne;
    }

    public void setMaestriaCarne(int maestriaCarne) {
        this.maestriaCarne = maestriaCarne;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public int getAdministrador() {
        return administrador;
    }

    public void setAdministrador(int administrador) {
        this.administrador = administrador;
    }
}
