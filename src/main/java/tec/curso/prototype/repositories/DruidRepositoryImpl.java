package tec.curso.prototype.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tec.curso.prototype.dto.ProductSaleEventDto;
import tec.curso.prototype.dto.TicketSaleEventDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Repository
public class DruidRepositoryImpl implements DruidRepository {

    @Value("${druid.url}")
    private String druidUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final DateTimeFormatter SQL_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    private void ingerirDatos(String sql) {
        try {
            String endpoint = druidUrl + "/druid/v2/sql/task";
            String jsonPayload = String.format(
                    "{\"query\": \"%s\", \"context\": {\"maxNumTasks\": 2}}",
                    escapeJson(sql)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("Error al ingestar en Druid. Status: " + response.statusCode());
                System.err.println("Respuesta del servidor: " + response.body());
                System.err.println("SQL enviado: " + sql);
            }

        } catch (Exception e) {
            System.err.println("Excepción al enviar tarea de ingesta a Druid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void ingestTicketSale(TicketSaleEventDto event) {
        String tituloEscapado = escapeSql(event.getTituloPelicula());
        String timestampStr = SQL_TIMESTAMP_FORMATTER.format(Instant.parse(event.getTimestamp()));
        String sql = String.format(Locale.US,
                "INSERT INTO \"ventas_taquilla\" " +
                        "SELECT " +
                        "  TIMESTAMP '%s' AS __time, " +
                        "  '%s' AS titulo_pelicula, " +
                        "  %d AS cantidad_tiquetes, " +
                        "  %.2f AS precio_unitario, " +
                        "  %.2f AS ingreso_bruto " +
                        "PARTITIONED BY DAY",
                timestampStr,
                tituloEscapado,
                event.getCantidadTiquetes(),
                event.getPrecioUnitario(),
                event.getIngresoBruto()
        );
        ingerirDatos(sql);
    }

    @Override
    public void ingestProductSale(ProductSaleEventDto event) {
        String nombreEscapado = escapeSql(event.getNombreProducto());
        String timestampStr = SQL_TIMESTAMP_FORMATTER.format(Instant.parse(event.getTimestamp()));
        String sql = String.format(Locale.US,
                "INSERT INTO \"ventas_dulceria\" " +
                        "SELECT " +
                        "  TIMESTAMP '%s' AS __time, " +
                        "  '%s' AS nombre_producto, " +
                        "  %d AS cantidad_vendida, " +
                        "  %.2f AS precio_unitario, " +
                        "  %.2f AS ingreso_bruto " +
                        "PARTITIONED BY DAY",
                timestampStr,
                nombreEscapado,
                event.getCantidadVendida(),
                event.getPrecioUnitario(),
                event.getIngresoBruto()
        );
        ingerirDatos(sql);
    }

    @Override
    public String consultarDatos(String sql) {
        String endpoint = druidUrl + "/druid/v2/sql";
        try {
            String jsonPayload = String.format("{\"query\": \"%s\"}", escapeJson(sql));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                System.err.println("Error al consultar Druid. Status: " + response.statusCode());
                System.err.println("Respuesta del servidor: " + response.body());
                System.err.println("SQL enviado: " + sql);
                return "[]";
            }

        } catch (Exception e) {
            System.err.println("Excepción al consultar Druid: " + e.getMessage());
            return "[]";
        }
    }

    private String escapeSql(String value) {
        return value != null ? value.replace("'", "''") : "";
    }

    private String escapeJson(String value) {
        return value != null ? value.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    }
}