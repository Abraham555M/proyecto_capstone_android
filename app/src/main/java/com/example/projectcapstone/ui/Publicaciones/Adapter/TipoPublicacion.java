package com.example.projectcapstone.ui.Publicaciones.Adapter;

public class TipoPublicacion {
    private String id_tipo_publicacion;
    private String nom_tipo_publicacion;

    public TipoPublicacion(String id, String nombre) {
        this.id_tipo_publicacion = id;
        this.nom_tipo_publicacion = nombre;
    }

    public String getId_tipo_publicacion() {
        return id_tipo_publicacion;
    }

    public String getNom_tipo_publicacion() {
        return nom_tipo_publicacion;
    }

    @Override
    public String toString() {
        return nom_tipo_publicacion; // Lo que se mostrará en el Spinner
    }
}
