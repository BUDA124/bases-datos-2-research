package tec.curso.prototype.dto;

/**
 * DTO para registrar la venta de un producto de inventario específico.
 * Se usa en el módulo "Inventario Inteligente".
 *
 * @param producto El nombre del producto vendido (ej. "Palomitas Grandes").
 * @param cantidad La cantidad de unidades vendidas de ese producto.
 */
public record VentaProductoDto(
    String producto,
    int cantidad
) {}