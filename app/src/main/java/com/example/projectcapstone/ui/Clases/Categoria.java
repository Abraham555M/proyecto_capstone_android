package com.example.projectcapstone.ui.Clases;

public class Categoria {
    private Integer idCategoria;
    private String nomCategoria;
    private String imgCategoria;

    public Categoria(Integer idCategoria, String nomCategoria, String imgCategoria) {
        this.idCategoria = idCategoria;
        this.nomCategoria = nomCategoria;
        this.imgCategoria = imgCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    public String getImgCategoria() {
        return imgCategoria;
    }

    public void setImgCategoria(String imgCategoria) {
        this.imgCategoria = imgCategoria;
    }
}
