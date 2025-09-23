package com.example.projectcapstone.ui.Clases;

public class Publicacion {
    private Integer idPublicacion;
    private String nomEmprendimiento;
    private String imgEmprendimiento;
    private String titPublicacion;
    private String conPublicacion;
    private String imgPublicacion;
    private Integer totalInteracciones;
    private Integer dioLike; // 1 si dio like, 0 si no

    public Publicacion(Integer idPublicacion, String nomEmprendimiento, String imgEmprendimiento,
                       String titPublicacion, String conPublicacion, String imgPublicacion,
                       Integer totalInteracciones, Integer dioLike) {
        this.idPublicacion = idPublicacion;
        this.nomEmprendimiento = nomEmprendimiento;
        this.imgEmprendimiento = imgEmprendimiento;
        this.titPublicacion = titPublicacion;
        this.conPublicacion = conPublicacion;
        this.imgPublicacion = imgPublicacion;
        this.totalInteracciones = totalInteracciones;
        this.dioLike = dioLike;
    }

    public Integer getDioLike() {
        return dioLike;
    }

    public void setDioLike(Integer dioLike) {
        this.dioLike = dioLike;
    }

    // 🔥 este es el que usas en el adapter
    public boolean isLiked() {
        return dioLike != null && dioLike == 1;
    }

    public void setLiked(boolean liked) {
        this.dioLike = liked ? 1 : 0;
    }

    public Integer getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(Integer idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getNomEmprendimiento() {
        return nomEmprendimiento;
    }

    public void setNomEmprendimiento(String nomEmprendimiento) {
        this.nomEmprendimiento = nomEmprendimiento;
    }

    public String getImgEmprendimiento() {
        return imgEmprendimiento;
    }

    public void setImgEmprendimiento(String imgEmprendimiento) {
        this.imgEmprendimiento = imgEmprendimiento;
    }

    public String getTitPublicacion() {
        return titPublicacion;
    }

    public void setTitPublicacion(String titPublicacion) {
        this.titPublicacion = titPublicacion;
    }

    public String getConPublicacion() {
        return conPublicacion;
    }

    public void setConPublicacion(String conPublicacion) {
        this.conPublicacion = conPublicacion;
    }

    public String getImgPublicacion() {
        return imgPublicacion;
    }

    public void setImgPublicacion(String imgPublicacion) {
        this.imgPublicacion = imgPublicacion;
    }

    public Integer getTotalInteracciones() {
        return totalInteracciones;
    }

    public void setTotalInteracciones(Integer totalInteracciones) {
        this.totalInteracciones = totalInteracciones;
    }
}
