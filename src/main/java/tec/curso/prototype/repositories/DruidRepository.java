package tec.curso.prototype.repositories;

import tec.curso.prototype.dto.ProductSaleEventDto;
import tec.curso.prototype.dto.TicketSaleEventDto;

/**
 * Interfaz que define las operaciones de acceso a datos para la ingesta en Druid.
 */
public interface DruidRepository {

    /**
     * Envía una consulta SQL de ingesta de datos a Druid.
     *
     * @param sql la sentencia SQL (INSERT) a ejecutar.
     */
    void ingerirDatos(String sql);
    /**
     * Envía una consulta SQL de lectura (SELECT) a Druid.
     *
     * @param sql la sentencia SQL (SELECT) a ejecutar.
     * @return una cadena de texto (String) con la respuesta en formato JSON de Druid.
     */
    String consultarDatos(String sql);

    void ingestProductSale(ProductSaleEventDto event);
    void ingestTicketSale(TicketSaleEventDto event);
}