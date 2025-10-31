package tec.curso.prototype.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaPeliculaDto;
import tec.curso.prototype.repositories.DruidRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestiona la lógica de negocio para el módulo "El Poder de Venta de las Películas".
 */
@Service
public class MovieSalesService {

    private final DruidRepository druidRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MovieSalesService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Registra una venta de dulcería asociada a una película específica.
     * @param venta DTO con el título de la película y el monto.
     */
    public void registrarVentaAsociadaAPelicula(VentaPeliculaDto venta) {
        String sql = String.format(
                "INSERT INTO ventas_dulceria_por_pelicula (__time, pelicula_titulo, monto_dulceria) VALUES (TIME_PARSE('%s'), '%s', %f)",
                Instant.now().toString(),
                venta.pelicula(),
                venta.monto()
        );
        druidRepository.ingerirDatos(sql);
        System.out.println("Procesada venta de dulcería de $" + venta.monto() + " para la película '" + venta.pelicula() + "'");
    }

    /**
     * **NUEVO MÉTODO**
     * Calcula el gasto promedio en dulcería por espectador para cada película.
     *
     * @return Una lista de mapas ordenada por el gasto promedio, conteniendo "pelicula_titulo" y "gasto_promedio".
     */
    public List<Map<String, Object>> obtenerRankingGastoPromedio() {
        try {
            // Paso 1: Obtener el total de ingresos de dulcería por película.
            String sqlDulceria = "SELECT pelicula_titulo, SUM(monto_dulceria) AS total_dulceria " +
                                 "FROM ventas_dulceria_por_pelicula GROUP BY pelicula_titulo";
            String jsonDulceria = druidRepository.consultarDatos(sqlDulceria);
            List<Map<String, Object>> ventasDulceria = objectMapper.readValue(jsonDulceria, new TypeReference<>() {});

            // Paso 2: Obtener el total de espectadores (entradas vendidas) por película.
            String sqlEspectadores = "SELECT pelicula_titulo, SUM(entradas_vendidas) AS total_espectadores " +
                                     "FROM ocupacion_funciones GROUP BY pelicula_titulo";
            String jsonEspectadores = druidRepository.consultarDatos(sqlEspectadores);
            List<Map<String, Object>> ventasEntradas = objectMapper.readValue(jsonEspectadores, new TypeReference<>() {});

            // Convertir la lista de espectadores a un mapa para búsqueda rápida.
            Map<String, Integer> espectadoresPorPelicula = ventasEntradas.stream()
                    .collect(Collectors.toMap(
                            map -> (String) map.get("pelicula_titulo"),
                            map -> ((Number) map.get("total_espectadores")).intValue()
                    ));

            // Paso 3: Combinar los datos y calcular el promedio en Java.
            List<Map<String, Object>> ranking = new ArrayList<>();
            for (Map<String, Object> venta : ventasDulceria) {
                String titulo = (String) venta.get("pelicula_titulo");
                double totalDulceria = ((Number) venta.get("total_dulceria")).doubleValue();
                int totalEspectadores = espectadoresPorPelicula.getOrDefault(titulo, 0);

                // Evitar división por cero. Si no hay espectadores, el promedio es 0.
                double gastoPromedio = (totalEspectadores > 0) ? totalDulceria / totalEspectadores : 0.0;

                Map<String, Object> resultadoFila = new HashMap<>();
                resultadoFila.put("pelicula_titulo", titulo);
                resultadoFila.put("gasto_promedio", gastoPromedio);
                ranking.add(resultadoFila);
            }

            // Ordenar el ranking de mayor a menor gasto promedio.
            ranking.sort((a, b) -> Double.compare((Double) b.get("gasto_promedio"), (Double) a.get("gasto_promedio")));

            return ranking;

        } catch (Exception e) {
            System.err.println("Error al calcular el ranking de gasto promedio: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}