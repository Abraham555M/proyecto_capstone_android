package com.example.projectcapstone.ui.Clases;

public class PublicacionPerfil {
    private String id;
    private String imageUrl;
    private String titulo;

    public PublicacionPerfil() {
        // Constructor vacío requerido para Firebase
    }

    public PublicacionPerfil(String id, String imageUrl, String titulo) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
