// Ejemplo para ProductSaleEventDto.java (haz lo mismo para TicketSaleEventDto)
package tec.curso.prototype.dto;

import java.time.Instant;

public class ProductSaleEventDto {

    private String timestamp;
    private String nombreProducto;
    private int cantidadVendida;
    private double precioUnitario;
    private double ingresoBruto;

    // Constructor para la venta en tiempo real (usado por SalesAreaService)
    public ProductSaleEventDto(String nombreProducto, int cantidadVendida, double precioUnitario) {
        this.timestamp = Instant.now().toString(); // El timestamp se genera aquí
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadVendida * precioUnitario;
    }

    public ProductSaleEventDto(Instant timestamp, String nombreProducto, int cantidadVendida, double precioUnitario) {
        this.timestamp = timestamp.toString(); // El timestamp se recibe como parámetro
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.precioUnitario = precioUnitario;
        this.ingresoBruto = cantidadVendida * precioUnitario;
    }

    public ProductSaleEventDto() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
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