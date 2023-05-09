package com.example.oteloxtfgdam.db;

public class ItemDB {
    private int id;
    private String nombre;
    private long fecha;
    private long precio;

    public ItemDB(int id, String nombre, long fecha, long precio) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
    }
    public int getId() {
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


}

