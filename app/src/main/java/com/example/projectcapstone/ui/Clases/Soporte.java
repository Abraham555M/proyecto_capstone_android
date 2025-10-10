package com.example.projectcapstone.ui.Clases;

public class Soporte {
    private int id_soporte;
    private int id_estudiante;
    private String men_soporte;
    private String fec_soporte;
    private String est_soporte;

    public Soporte(int id_soporte, int id_estudiante, String men_soporte, String fec_soporte, String est_soporte) {
        this.id_soporte = id_soporte;
        this.id_estudiante = id_estudiante;
        this.men_soporte = men_soporte;
        this.fec_soporte = fec_soporte;
        this.est_soporte = est_soporte;
    }

    public int getId_soporte() { return id_soporte; }
    public int getId_estudiante() { return id_estudiante; }
    public String getMen_soporte() { return men_soporte; }
    public String getFec_soporte() { return fec_soporte; }
    public String getEst_soporte() { return est_soporte; }
}