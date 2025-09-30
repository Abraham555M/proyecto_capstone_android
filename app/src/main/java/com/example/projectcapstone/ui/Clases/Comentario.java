package com.example.projectcapstone.ui.Clases;

public class Comentario {
    private int idComentario;
    private String conComentario;
    private String fchComentario;
    private String nomEstudiante;

    public Comentario(int idComentario, String conComentario, String fchComentario, String nomEstudiante) {
        this.idComentario = idComentario;
        this.conComentario = conComentario;
        this.fchComentario = fchComentario;
        this.nomEstudiante = nomEstudiante;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getConComentario() {
        return conComentario;
    }

    public void setConComentario(String conComentario) {
        this.conComentario = conComentario;
    }

    public String getFchComentario() {
        return fchComentario;
    }

    public void setFchComentario(String fchComentario) {
        this.fchComentario = fchComentario;
    }

    public String getNomEstudiante() {
        return nomEstudiante;
    }

    public void setNomEstudiante(String nomEstudiante) {
        this.nomEstudiante = nomEstudiante;
    }
}
