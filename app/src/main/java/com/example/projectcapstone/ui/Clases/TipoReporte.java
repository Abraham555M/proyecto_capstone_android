package com.example.projectcapstone.ui.Clases;

public class TipoReporte {
    private Integer idTipoReporte;
    private String nomTipoReporte;

    public TipoReporte(Integer idTipoReporte, String nomTipoReporte) {
        this.idTipoReporte = idTipoReporte;
        this.nomTipoReporte = nomTipoReporte;
    }

    public Integer getIdTipoReporte() {
        return idTipoReporte;
    }

    public void setIdTipoReporte(Integer idTipoReporte) {
        this.idTipoReporte = idTipoReporte;
    }

    public String getNomTipoReporte() {
        return nomTipoReporte;
    }

    public void setNomTipoReporte(String nomTipoReporte) {
        this.nomTipoReporte = nomTipoReporte;
    }
}
