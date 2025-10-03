package com.example.projectcapstone.ui.Emprendimiento;

public class Emprendimiento {
    private String id_emprendimiento;
    private String id_estudiante;
    private String id_categoria;
    private String nom_emprendimiento;
    private String des_emprendimiento;
    private String img_por_emprendimiento;
    private String img_per_emprendimiento;
    private int est_emprendimiento;

    public Emprendimiento(String id_emprendimiento, String id_estudiante, String id_categoria,
                          String nom_emprendimiento, String des_emprendimiento,
                          String img_por_emprendimiento, String img_per_emprendimiento,
                          int est_emprendimiento) {
        this.id_emprendimiento = id_emprendimiento;
        this.id_estudiante = id_estudiante;
        this.id_categoria = id_categoria;
        this.nom_emprendimiento = nom_emprendimiento;
        this.des_emprendimiento = des_emprendimiento;
        this.img_por_emprendimiento = img_por_emprendimiento;
        this.img_per_emprendimiento = img_per_emprendimiento;
        this.est_emprendimiento = est_emprendimiento;
    }

    // getters (los importantes)
    public String getId_emprendimiento() { return id_emprendimiento; }
    public String getId_estudiante() { return id_estudiante; }
    public String getId_categoria() { return id_categoria; }
    public String getNom_emprendimiento() { return nom_emprendimiento; }
    public String getDes_emprendimiento() { return des_emprendimiento; }
    public String getImg_por_emprendimiento() { return img_por_emprendimiento; }
    public String getImg_per_emprendimiento() { return img_per_emprendimiento; }
    public int getEst_emprendimiento() { return est_emprendimiento; }

    // opcional: alias en camelCase por compatibilidad con otros estilos
    public String getImgPerEmprendimiento() { return img_per_emprendimiento; }
    public String getNomEmprendimiento() { return nom_emprendimiento; }
}
