package com.example.oteloxtfgdam.db;

import org.bson.types.ObjectId;

public class ItemsDB {
    private ObjectId id;
    private String nombre;
    private String itemId;
    private String grado;
    private String imagen;

    public ItemsDB(){

    }

    public ItemsDB(ObjectId id, String nombre, String itemId, String grado, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.itemId = itemId;
        this.grado = grado;
        this.imagen = imagen;
    }

    public ObjectId getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getItemId() {
        return itemId;
    }

    public String getGrado() {
        return grado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
