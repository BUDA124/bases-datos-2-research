// Archivo: src/main/java/tec/curso/prototype/services/IngestionService.java

package tec.curso.prototype.services;

import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@Service
public class IngestionService {

    // 1. Cliente HTTP para comunicarnos con la API de Druid.
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Método para el Módulo "Pulso Financiero del Día"
    public void registrarVentaFinanciera(VentaDto venta) {
        // Usamos el datasource 'ingresos_diarios'
        String sql = String.format(
                "INSERT INTO ingresos_diarios (__time, fuente_ingreso, monto_venta) VALUES (TIME_PARSE('%s'), '%s', %f)",
                Instant.now().toString(),
                venta.tipo(), // "Taquilla" o "Dulceria"
                venta.monto()
        );
        enviarADruid(sql);
        System.out.println("Enviada a Druid venta de " + venta.tipo() + " por $" + venta.monto());
    }

    // Método para el Módulo "El Poder de Venta de las Películas"
    public void registrarVentaAsociadaAPelicula(VentaPeliculaDto venta) {
        // Usamos el datasource 'ventas_dulceria_por_pelicula'
        String sql = String.format(
                "INSERT INTO ventas_dulceria_por_pelicula (__time, pelicula_titulo, monto_dulceria) VALUES (TIME_PARSE('%s'), '%s', %f)",
                Instant.now().toString(),
                venta.pelicula(),
                venta.monto()
        );
        enviarADruid(sql);
        System.out.println("Enviada a Druid venta de dulcería de $" + venta.monto() + " para la película '" + venta.pelicula() + "'");
    }

    // Método para el Módulo "Ocupación y Oportunidad por Función"
    public void registrarVentaDeEntradas(VentaFuncionDto venta) { // <-- Ahora usa el DTO completo
        String sql = String.format(
                "INSERT INTO ocupacion_funciones (__time, sala_nombre, pelicula_titulo, horario_funcion, capacidad_sala, entradas_vendidas) VALUES (TIME_PARSE('%s'), '%s', '%s', '%s', %d, %d)",
                Instant.now().toString(),
                venta.salaNombre(),
                venta.peliculaTitulo(),
                venta.horarioFuncion(),
                venta.capacidadSala(),
                venta.entradasVendidas()
        );
        enviarADruid(sql);
        System.out.println("Enviadas a Druid " + venta.entradasVendidas() + " entradas para la función '" + venta.peliculaTitulo() + "' en la " + venta.salaNombre());
    }

    // Método para el Módulo "Inventario Inteligente"
    public void registrarVentaDeProducto(VentaProductoDto venta) {
        String sql = String.format(
                "INSERT INTO ventas_productos_inventario (__time, producto_nombre, cantidad_vendida) VALUES (TIME_PARSE('%s'), '%s', %d)",
                Instant.now().toString(),
                venta.producto(),
                venta.cantidad()
        );
        enviarADruid(sql);
        System.out.println("Enviada a Druid venta de " + venta.cantidad() + " unidad(es) de '" + venta.producto() + "'");
    }

    /**
     * Método auxiliar privado que construye y envía la petición HTTP a Druid.
     * @param sql La sentencia INSERT completa a ejecutar.
     */
    private void enviarADruid(String sql) {
        // Creamos el cuerpo de la petición en formato JSON.
        String jsonBody = "{\"query\":\"" + sql.replace("\"", "\\\"") + "\"}";

        // Construimos la petición HTTP POST.
        // 2. Endpoint del Router de Druid, según tu docker-compose.yml
        String druidSqlApiEndpoint = "http://localhost:8888/druid/v2/sql/";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(druidSqlApiEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            // Enviamos la petición y esperamos la respuesta.
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificamos si la ingesta fue exitosa (código 2xx).
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("Error al ingerir datos en Druid: " + response.statusCode());
                System.err.println("Respuesta: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Fallo al conectar con el Router de Druid: " + e.getMessage());
            Thread.currentThread().interrupt(); // Buena práctica
        }
    }
}