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
        String jsonBody = "{\"query\":\"" + sql.replace("\"", "\\\"") + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DRUID_SQL_API_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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

    @Override
    public String consultarDatos(String sql) {
        String jsonBody = "{\"query\":\"" + sql.replace("\"", "\\\"") + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DRUID_SQL_API_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Consulta a Druid exitosa.");
                return response.body(); // Devolvemos el JSON resultante
            } else {
                System.err.println("Error al consultar datos en Druid: " + response.statusCode());
                System.err.println("Respuesta: " + response.body());
                return "[]";
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Fallo al conectar con el Router de Druid: " + e.getMessage());
            Thread.currentThread().interrupt();
            return "[]";
        }
    }
}