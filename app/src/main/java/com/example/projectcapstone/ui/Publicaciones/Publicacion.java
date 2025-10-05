package com.example.projectcapstone.ui.Publicaciones;

public class Publicacion {
    private int id;
    private String titulo;
    private String descripcion;
    private String imagenUrl;

    public Publicacion(int id, String titulo, String descripcion, String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
}
