package tec.curso.prototype.dto;

import java.time.Instant;

public class TicketSaleEventDto {

    private String timestamp;
    private String tituloPelicula;
    private int cantidadTiquetes;
    private double precioUnitario; // Precio por tiquete
    private double ingresoBruto;   // precioUnitario * cantidadTiquetes

    public TicketSaleEventDto(String tituloPelicula, int cantidadTiquetes, double precioUnitario) {
        this.timestamp = Instant.now().toString();
        this.tituloPelicula = tituloPelicula;
        this.cantidadTiquetes = cantidadTiquetes;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadTiquetes * precioUnitario;
    }

    public TicketSaleEventDto(Instant timestamp, String tituloPelicula, int cantidadTiquetes, double precioUnitario) {
        this.timestamp = timestamp.toString();
        this.tituloPelicula = tituloPelicula;
        this.cantidadTiquetes = cantidadTiquetes;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadTiquetes * precioUnitario;
    }

    public TicketSaleEventDto() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public int getCantidadTiquetes() {
        return cantidadTiquetes;
    }

    public void setCantidadTiquetes(int cantidadTiquetes) {
        this.cantidadTiquetes = cantidadTiquetes;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getIngresoBruto() {
        return ingresoBruto;
    }

    public void setIngresoBruto(double ingresoBruto) {
        this.ingresoBruto = ingresoBruto;
    }
}