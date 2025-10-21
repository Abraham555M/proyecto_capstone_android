package com.example.projectcapstone.ui.Clases;

public class Notificacion {
    private int id_notificacion;
    private String titulo;
    private String mensaje;
    private String fecha;
    private int leida;
    private String tipo;
    private String nombre_emisor;
    private String correo_emisor;

    // Constructor vacío (necesario para Gson, Jackson, etc.)
    public Notificacion() {
    }

    // Constructor con parámetros (opcional)
    public Notificacion(int id_notificacion, String titulo, String mensaje, String fecha,
                        int leida, String tipo, String nombre_emisor, String correo_emisor) {
        this.id_notificacion = id_notificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = leida;
        this.tipo = tipo;
        this.nombre_emisor = nombre_emisor;
        this.correo_emisor = correo_emisor;
    }

    // Getters y Setters
    public int getId_notificacion() {
        return id_notificacion;
    }

    public void setId_notificacion(int id_notificacion) {
        this.id_notificacion = id_notificacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getLeida() {
        return leida;
    }

    public void setLeida(int leida) {
        this.leida = leida;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre_emisor() {
        return nombre_emisor;
    }

    public void setNombre_emisor(String nombre_emisor) {
        this.nombre_emisor = nombre_emisor;
    }

    public String getCorreo_emisor() {
        return correo_emisor;
    }

    public void setCorreo_emisor(String correo_emisor) {
        this.correo_emisor = correo_emisor;
    }
}
