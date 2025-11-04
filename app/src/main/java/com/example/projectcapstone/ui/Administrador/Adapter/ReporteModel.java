package com.example.projectcapstone.ui.Administrador.Adapter;

public class ReporteModel {
    private int idReporte;
    private String usuarioReporta;
    private String motivo;
    private String titulo;
    private String contenido;
    private String fecha;
    private String estado;

    public ReporteModel(int idReporte, String usuarioReporta, String motivo, String titulo,
                        String contenido, String fecha, String estado) {
        this.idReporte = idReporte;
        this.usuarioReporta = usuarioReporta;
        this.motivo = motivo;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdReporte() { return idReporte; }
    public String getUsuarioReporta() { return usuarioReporta; }
    public String getMotivo() { return motivo; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
}
