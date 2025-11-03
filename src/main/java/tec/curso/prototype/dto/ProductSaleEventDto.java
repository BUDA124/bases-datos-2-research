package tec.curso.prototype.dto;

import java.time.Instant;

public class ProductSaleEventDto {

    private String timestamp = Instant.now().toString();
    private String nombreProducto;
    private int cantidadVendida;
    private double precioUnitario; // Precio por producto
    private double ingresoBruto;   // precioUnitario * cantidadVendida

    // Constructor, Getters y Setters...

    public ProductSaleEventDto(String nombreProducto, int cantidadVendida, double precioUnitario) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadVendida * precioUnitario;
    }

    public double getIngresoBruto() {
        return ingresoBruto;
    }

    public void setIngresoBruto(double ingresoBruto) {
        this.ingresoBruto = ingresoBruto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}