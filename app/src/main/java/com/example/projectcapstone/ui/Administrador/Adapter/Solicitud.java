package com.example.projectcapstone.ui.Administrador.Adapter;

public class Solicitud {
    private int id_soporte;
    private String nom_estudiante;
    private String men_soporte;
    private String fec_soporte;
    private String est_soporte;

    // Getters y setters
    public int getId_soporte() { return id_soporte; }
    public void setId_soporte(int id_soporte) { this.id_soporte = id_soporte; }

    public String getNom_estudiante() { return nom_estudiante; }
    public void setNom_estudiante(String nom_estudiante) { this.nom_estudiante = nom_estudiante; }

    public String getMen_soporte() { return men_soporte; }
    public void setMen_soporte(String men_soporte) { this.men_soporte = men_soporte; }

    public String getFec_soporte() { return fec_soporte; }
    public void setFec_soporte(String fec_soporte) { this.fec_soporte = fec_soporte; }

    public String getEst_soporte() { return est_soporte; }
    public void setEst_soporte(String est_soporte) { this.est_soporte = est_soporte; }
}
