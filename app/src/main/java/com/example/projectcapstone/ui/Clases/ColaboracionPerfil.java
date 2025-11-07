package com.example.projectcapstone.ui.Clases;

public class ColaboracionPerfil {

    private int idColaboracion;

    private String nomEstudiante;

    private String apePatEstudiante;

    private String apeMatEstudiante;

    private String emaEstudiante;

    private String fchColaboracion;

    private int estColaboracion;

    private String estadoTexto;

    // Constructor vacío
    public ColaboracionPerfil() {
    }

    // Constructor completo
    public ColaboracionPerfil(int idColaboracion, String nomEstudiante, String apePatEstudiante,
                              String apeMatEstudiante, String emaEstudiante, String fchColaboracion,
                              int estColaboracion, String estadoTexto) {
        this.idColaboracion = idColaboracion;
        this.nomEstudiante = nomEstudiante;
        this.apePatEstudiante = apePatEstudiante;
        this.apeMatEstudiante = apeMatEstudiante;
        this.emaEstudiante = emaEstudiante;
        this.fchColaboracion = fchColaboracion;
        this.estColaboracion = estColaboracion;
        this.estadoTexto = estadoTexto;
    }

    // Getters
    public int getIdColaboracion() {
        return idColaboracion;
    }

    public String getNomEstudiante() {
        return nomEstudiante;
    }

    public String getApePatEstudiante() {
        return apePatEstudiante;
    }

    public String getApeMatEstudiante() {
        return apeMatEstudiante;
    }

    public String getEmaEstudiante() {
        return emaEstudiante;
    }

    public String getFchColaboracion() {
        return fchColaboracion;
    }

    public int getEstColaboracion() {
        return estColaboracion;
    }

    public String getEstadoTexto() {
        return estadoTexto;
    }

    // Setters
    public void setIdColaboracion(int idColaboracion) {
        this.idColaboracion = idColaboracion;
    }

    public void setNomEstudiante(String nomEstudiante) {
        this.nomEstudiante = nomEstudiante;
    }

    public void setApePatEstudiante(String apePatEstudiante) {
        this.apePatEstudiante = apePatEstudiante;
    }

    public void setApeMatEstudiante(String apeMatEstudiante) {
        this.apeMatEstudiante = apeMatEstudiante;
    }

    public void setEmaEstudiante(String emaEstudiante) {
        this.emaEstudiante = emaEstudiante;
    }

    public void setFchColaboracion(String fchColaboracion) {
        this.fchColaboracion = fchColaboracion;
    }

    public void setEstColaboracion(int estColaboracion) {
        this.estColaboracion = estColaboracion;
    }

    public void setEstadoTexto(String estadoTexto) {
        this.estadoTexto = estadoTexto;
    }
}