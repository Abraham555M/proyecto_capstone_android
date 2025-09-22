package com.example.projectcapstone.ui.Clases;

public class Publicacion {
    private Integer idPublicacion;
    private String nomEmprendimiento;
    private String imgEmprendimiento;
    private String titPublicacion;
    private String conPublicacion;
    private String imgPublicacion;
    private  Integer totalInteracciones;

    public Publicacion(Integer idPublicacion, String nomEmprendimiento, String imgEmprendimiento, String titPublicacion, String conPublicacion, String imgPublicacion, Integer totalInteracciones) {
        this.idPublicacion = idPublicacion;
        this.nomEmprendimiento = nomEmprendimiento;
        this.imgEmprendimiento = imgEmprendimiento;
        this.titPublicacion = titPublicacion;
        this.conPublicacion = conPublicacion;
        this.imgPublicacion = imgPublicacion;
        this.totalInteracciones = totalInteracciones;
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
