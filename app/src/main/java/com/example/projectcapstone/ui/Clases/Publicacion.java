package com.example.projectcapstone.ui.Clases;

public class Publicacion {

    // 🔹 Campos generales
    private Integer idPublicacion;
    private Integer idEmprendimiento;
    private String nomEmprendimiento;
    private String imgEmprendimiento;
    private String titPublicacion;
    private String conPublicacion;
    private String imgPublicacion;
    private Integer totalInteracciones;
    private Integer dioLike;
    private Integer dioSeguimiento;
    private Integer esFavorito;
    private Integer tipoPublicacion; // 1=Producto,2=Promocion,3=Evento

    // 🔹 Campos específicos según tipo
    private Producto producto;
    private Promocion promocion;
    private Evento evento;

    // Constructor general
    public Publicacion(Integer idPublicacion, Integer idEmprendimiento, String nomEmprendimiento, String imgEmprendimiento,
                       String titPublicacion, String conPublicacion, String imgPublicacion, Integer totalInteracciones,
                       Integer dioLike, Integer dioSeguimiento, Integer esFavorito, Integer tipoPublicacion,
                       Producto producto, Evento evento, Promocion promocion) {
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
        this.tipoPublicacion = tipoPublicacion;
        this.producto = producto;
        this.evento = evento;
        this.promocion = promocion;
    }

    // Constructor para métricas
    public Publicacion(Integer idPublicacion, Integer idEmprendimiento, String nomEmprendimiento, String imgEmprendimiento,
                       String titPublicacion, String conPublicacion, String imgPublicacion, Integer totalInteracciones) {
        this.idPublicacion = idPublicacion;
        this.idEmprendimiento = idEmprendimiento;
        this.nomEmprendimiento = nomEmprendimiento;
        this.imgEmprendimiento = imgEmprendimiento;
        this.titPublicacion = titPublicacion;
        this.conPublicacion = conPublicacion;
        this.imgPublicacion = imgPublicacion;
        this.totalInteracciones = totalInteracciones;
    }

    // 🔥 Métodos de estado
    public boolean isFavorito() {
        return esFavorito != null && esFavorito == 1;
    }

    public void setFavorito(boolean favorito) {
        this.esFavorito = favorito ? 1 : 0;
    }

    public boolean isLiked() {
        return dioLike != null && dioLike == 1;
    }

    public void setLiked(boolean liked) {
        this.dioLike = liked ? 1 : 0;
    }

    public boolean isSiguiendo() {
        return dioSeguimiento != null && dioSeguimiento == 1;
    }

    public void setSiguiendo(boolean siguiendo) {
        this.dioSeguimiento = siguiendo ? 1 : 0;
    }

    // 🔹 Getters y setters generales
    public Integer getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Integer idPublicacion) { this.idPublicacion = idPublicacion; }

    public Integer getIdEmprendimiento() { return idEmprendimiento; }
    public void setIdEmprendimiento(Integer idEmprendimiento) { this.idEmprendimiento = idEmprendimiento; }

    public String getNomEmprendimiento() { return nomEmprendimiento; }
    public void setNomEmprendimiento(String nomEmprendimiento) { this.nomEmprendimiento = nomEmprendimiento; }

    public String getImgEmprendimiento() { return imgEmprendimiento; }
    public void setImgEmprendimiento(String imgEmprendimiento) { this.imgEmprendimiento = imgEmprendimiento; }

    public String getTitPublicacion() { return titPublicacion; }
    public void setTitPublicacion(String titPublicacion) { this.titPublicacion = titPublicacion; }

    public String getConPublicacion() { return conPublicacion; }
    public void setConPublicacion(String conPublicacion) { this.conPublicacion = conPublicacion; }

    public String getImgPublicacion() { return imgPublicacion; }
    public void setImgPublicacion(String imgPublicacion) { this.imgPublicacion = imgPublicacion; }

    public Integer getTotalInteracciones() { return totalInteracciones; }
    public void setTotalInteracciones(Integer totalInteracciones) { this.totalInteracciones = totalInteracciones; }

    public Integer getDioLike() { return dioLike; }
    public void setDioLike(Integer dioLike) { this.dioLike = dioLike; }

    public Integer getDioSeguimiento() { return dioSeguimiento; }
    public void setDioSeguimiento(Integer dioSeguimiento) { this.dioSeguimiento = dioSeguimiento; }

    public Integer getEsFavorito() { return esFavorito; }
    public void setEsFavorito(Integer esFavorito) { this.esFavorito = esFavorito; }

    public Integer getTipoPublicacion() { return tipoPublicacion; }
    public void setTipoPublicacion(Integer tipoPublicacion) { this.tipoPublicacion = tipoPublicacion; }

    // 🔹 Getters y setters específicos
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Promocion getPromocion() { return promocion; }
    public void setPromocion(Promocion promocion) { this.promocion = promocion; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    // 🔹 Clases internas para los tipos específicos
    public static class Producto {
        private Double precio;
        private Integer stock;

        public Producto(Double precio, Integer stock) {
            this.precio = precio;
            this.stock = stock;
        }

        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class Promocion {
        private String descripcion;
        private String fechaInicio;
        private String fechaFin;

        public Promocion(String descripcion, String fechaInicio, String fechaFin) {
            this.descripcion = descripcion;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
        }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

        public String getFechaFin() { return fechaFin; }
        public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    }

    public static class Evento {
        private String fecha;
        private String lugar;

        public Evento(String fecha, String lugar) {
            this.fecha = fecha;
            this.lugar = lugar;
        }

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        public String getLugar() { return lugar; }
        public void setLugar(String lugar) { this.lugar = lugar; }
    }
}
