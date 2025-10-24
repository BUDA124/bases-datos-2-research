// Archivo: src/main/java/tec/curso/prototype/dto/VentaFuncionDto.java

package tec.curso.prototype.dto;

/**
 * DTO completo para registrar los detalles de una venta de entradas para una función específica.
 * A diferencia del DTO simple, este objeto contiene toda la información necesaria
 * para el registro, eliminando la necesidad de valores fijos en el servicio.
 * Se usa en el módulo "Ocupación y Oportunidad por Función".
 *
 * @param salaNombre       El nombre o identificador de la sala (ej. "Sala Simulada", "Sala 4").
 * @param peliculaTitulo   El título de la película que se proyecta en la función.
 * @param horarioFuncion   La hora de inicio de la función (ej. "20:00").
 * @param capacidadSala    La capacidad total de asientos de la sala.
 * @param entradasVendidas El número de entradas que se vendieron en esta transacción.
 */
public record VentaFuncionDto(
    String salaNombre,
    String peliculaTitulo,
    String horarioFuncion,
    int capacidadSala,
    int entradasVendidas
) {}