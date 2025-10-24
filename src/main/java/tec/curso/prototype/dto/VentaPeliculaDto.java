// Archivo: src/main/java/tec/curso/prototype/dto/VentaPeliculaDto.java

package tec.curso.prototype.dto;

/**
 * DTO para registrar una venta de dulcería asociada a una película específica.
 * Se usa en el módulo "El Poder de Venta de las Películas".
 *
 * @param pelicula El título de la película con la que se asocia la venta.
 * @param monto    El valor de la venta de dulcería.
 */
public record VentaPeliculaDto(
    String pelicula,
    double monto
) {}