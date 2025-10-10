package com.example.projectcapstone.ui.Colaboraciones;

import com.example.projectcapstone.ui.Publicaciones.Adapter.Publicacion;

public class Colaboracion {
    private int id_colaboracion;
    private int id_estudiante;
    private int id_emprendimiento;
    private int id_publicacion;
    private String men_colaboracion;
    private String fch_colaboracion;
    private int est_colaboracion;
    private Publicacion publicacion; // 🔹 agregado

    public Colaboracion(int id_colaboracion, int id_estudiante, int id_emprendimiento,
                        int id_publicacion, String men_colaboracion,
                        String fch_colaboracion, int est_colaboracion,
                        Publicacion publicacion) {
        this.id_colaboracion = id_colaboracion;
        this.id_estudiante = id_estudiante;
        this.id_emprendimiento = id_emprendimiento;
        this.id_publicacion = id_publicacion;
        this.men_colaboracion = men_colaboracion;
        this.fch_colaboracion = fch_colaboracion;
        this.est_colaboracion = est_colaboracion;
        this.publicacion = publicacion;
    }

    public int getId_colaboracion() { return id_colaboracion; }
    public int getId_estudiante() { return id_estudiante; }
    public int getId_emprendimiento() { return id_emprendimiento; }
    public int getId_publicacion() { return id_publicacion; }
    public String getMen_colaboracion() { return men_colaboracion; }
    public String getFch_colaboracion() { return fch_colaboracion; }
    public int getEst_colaboracion() { return est_colaboracion; }

    public void setEst_colaboracion(int est_colaboracion) {
        this.est_colaboracion = est_colaboracion;
    }
    public Publicacion getPublicacion() { return publicacion; }
}
