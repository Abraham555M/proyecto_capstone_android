package com.example.projectcapstone.ui.Publicaciones.Adapter;

public class CategoriaPublicacion {
    private int idCategoria;
    private int idEmprendimiento;
    private String nombre;
    private String imagen;

    public CategoriaPublicacion(int idCategoria, int idEmprendimiento, String nombre, String imagen) {
        this.idCategoria = idCategoria;
        this.idEmprendimiento = idEmprendimiento;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public int getIdCategoria() { return idCategoria; }
    public int getIdEmprendimiento() { return idEmprendimiento; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
}
