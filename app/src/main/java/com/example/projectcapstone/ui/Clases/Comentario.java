package com.example.projectcapstone.ui.Clases;

public class Comentario {
    private int idComentario;
    private String conComentario;
    private String fchComentario;
    private String nomEstudiante;
    private boolean liked;
    private int totalLikes;

    public Comentario(int idComentario, String conComentario, String fchComentario, String nomEstudiante, boolean liked, int totalLikes) {
        this.idComentario = idComentario;
        this.conComentario = conComentario;
        this.fchComentario = fchComentario;
        this.nomEstudiante = nomEstudiante;
        this.liked = liked;
        this.totalLikes = totalLikes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
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
