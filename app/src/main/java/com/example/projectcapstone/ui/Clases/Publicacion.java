package com.example.projectcapstone.ui.Clases;

public class Publicacion {
    private Integer idPublicacion;
    private Integer idEmprendimiento;
    private String nomEmprendimiento;
    private String imgEmprendimiento;
    private String titPublicacion;
    private String conPublicacion;
    private String imgPublicacion;
    private Integer totalInteracciones;
    private Integer dioLike; // 1 si dio like, 0 si no
    private Integer dioSeguimiento;
    private Integer esFavorito;

    public Publicacion(Integer idPublicacion, Integer idEmprendimiento, String nomEmprendimiento, String imgEmprendimiento, String titPublicacion, String conPublicacion, String imgPublicacion, Integer totalInteracciones, Integer dioLike, Integer dioSeguimiento, Integer esFavorito){
        this.idPublicacion = idPublicacion;
        this.idEmprendimiento = idEmprendimiento;
        this.nomEmprendimiento = nomEmprendimiento;
        this.imgEmprendimiento = imgEmprendimiento;
        this.titPublicacion = titPublicacion;
        this.conPublicacion = conPublicacion;
        this.imgPublicacion = imgPublicacion;
        this.totalInteracciones = totalInteracciones;
        this.dioLike = dioLike;
        this.dioSeguimiento = dioSeguimiento;
        this.esFavorito = esFavorito;
    }

    public Integer getEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(Integer esFavorito) {
        this.esFavorito = esFavorito;
    }

    // 🔥 útil en el adapter
    public boolean isFavorito() {
        return esFavorito != null && esFavorito == 1;
    }

    public void setFavorito(boolean favorito) {
        this.esFavorito = favorito ? 1 : 0;
    }

    public Integer getDioSeguimiento() {
        return dioSeguimiento;
    }

    public void setDioSeguimiento(Integer dioSeguimiento) {
        this.dioSeguimiento = dioSeguimiento;
    }

    public Integer getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(Integer idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
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
