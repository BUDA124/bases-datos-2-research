package tec.curso.prototype.repositories;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Repository
public class DruidRepositoryImpl implements DruidRepository {

    // Cliente HTTP para la comunicación con la API de Druid.
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Endpoint del Router de Druid. Idealmente, esto debería estar en application.properties.
    private static final String DRUID_SQL_API_ENDPOINT = "http://localhost:8888/druid/v2/sql/";

    @Override
    public void ingerirDatos(String sql) {
        // Creamos el cuerpo de la petición en formato JSON.
        String jsonBody = "{\"query\":\"" + sql.replace("\"", "\\\"") + "\"}";

        // Construimos la petición HTTP POST.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DRUID_SQL_API_ENDPOINT))
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
            } else {
                System.out.println("Datos ingeridos en Druid correctamente.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Fallo al conectar con el Router de Druid: " + e.getMessage());
            Thread.currentThread().interrupt(); // Buena práctica
        }
    }
}