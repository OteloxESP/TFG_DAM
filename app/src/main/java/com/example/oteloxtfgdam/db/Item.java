package com.example.oteloxtfgdam.db;

import org.bson.types.ObjectId;

public class Item {
    private ObjectId id;
    private String nombre;
    private long fecha;
    private long precio;
    private String grado;
    private String imagen;
    private Double itemId;
    public Item() {
        // Constructor sin argumentos requerido por el decodificador BSON
    }
    public Item(ObjectId id, String nombre, long fecha, long precio) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
    }

    public Item(ObjectId id, String grado, String imagen, Double itemId, String nombre) {
        this.id = id;
        this.grado = grado;
        this.imagen = imagen;
        this.itemId = itemId;
        this.nombre = nombre;
    }
    public Item(ObjectId id, String nombre, long fecha, long precio, String grado, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
        this.grado = grado;
        this.imagen = imagen;
    }
    public ObjectId getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public long getFecha() {
        return fecha;
    }

    public long getPrecio() {
        return precio;
    }

    public String getGrado(){
        return grado;
    }
    public String getImagen(){
        return imagen;
    }

    public Double getItemId(){
        return itemId;
    }

    @Override
    public String toString() {
        return "Plant" + imagen+"]";
    }
}

