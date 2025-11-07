package com.example.projectcapstone.ui.Clases;

import java.util.List;

public class EmprendimientoPerfil {
    private String idEmprendimiento;
    private String nombreCategoria;
    private List<PublicacionPerfil> publicaciones;

    public EmprendimientoPerfil() {
        // Constructor vacío requerido para Firebase
    }

    public EmprendimientoPerfil(String idEmprendimiento, String nombreCategoria, List<PublicacionPerfil> publicaciones) {
        this.idEmprendimiento = idEmprendimiento;
        this.nombreCategoria = nombreCategoria;
        this.publicaciones = publicaciones;
    }

    public String getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(String idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public List<PublicacionPerfil> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<PublicacionPerfil> publicaciones) {
        this.publicaciones = publicaciones;
    }
}
