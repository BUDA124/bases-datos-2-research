package tec.curso.prototype.dto;

import java.time.Instant;

public class TicketSaleEventDto {

    private String timestamp = Instant.now().toString();
    private String tituloPelicula;
    private int cantidadTiquetes;
    private double precioUnitario; // Precio por tiquete
    private double ingresoBruto;   // precioUnitario * cantidadTiquetes

    // Constructor, Getters y Setters...

    public TicketSaleEventDto(String tituloPelicula, int cantidadTiquetes, double precioUnitario) {
        this.tituloPelicula = tituloPelicula;
        this.cantidadTiquetes = cantidadTiquetes;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadTiquetes * precioUnitario;
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