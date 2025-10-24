package tec.curso.prototype.dto;

/**
 * DTO para registrar una venta financiera general.
 * Se usa en el módulo "Pulso Financiero del Día".
 *
 * @param tipo  La fuente del ingreso (ej. "Taquilla", "Dulceria").
 * @param monto El valor monetario de la venta.
 */
public record VentaDto(
    String tipo,
    double monto
) {}