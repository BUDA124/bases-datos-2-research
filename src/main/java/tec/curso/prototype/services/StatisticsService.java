package tec.curso.prototype.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.repositories.DruidRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final DruidRepository druidRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StatisticsService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Obtiene el ingreso total de la taquilla.
     */
    public double obtenerTotalIngresosTaquilla() {
        String sql = "SELECT SUM(ingreso_bruto) AS total FROM \"ventas_taquilla\"";
        return ejecutarConsultaDeSuma(sql, "Total Taquilla");
    }

    /**
     * Obtiene el ingreso total de la dulcería.
     */
    public double obtenerTotalIngresosDulceria() {
        String sql = "SELECT SUM(ingreso_bruto) AS total FROM \"ventas_dulceria\"";
        return ejecutarConsultaDeSuma(sql, "Total Dulcería");
    }

    /**
     * Obtiene el ranking de películas por número de espectadores.
     */
    public List<Map<String, Object>> obtenerRankingPeliculas() {
        String sql = "SELECT titulo_pelicula, SUM(cantidad_tiquetes) AS total_espectadores " +
                "FROM \"ventas_taquilla\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return ejecutarConsultaDeRanking(sql, "Ranking Películas");
    }

    /**
     * Obtiene el ranking de productos por cantidad vendida.
     */
    public List<Map<String, Object>> obtenerRankingProductos() {
        String sql = "SELECT nombre_producto, SUM(cantidad_vendida) AS total_vendido " +
                "FROM \"ventas_dulceria\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return ejecutarConsultaDeRanking(sql, "Ranking Productos");
    }

    // --- Métodos de ayuda privados para no repetir código ---

    private double ejecutarConsultaDeSuma(String sql, String contextLog) {
        try {
            String jsonResponse = druidRepository.consultarDatos(sql);
            JsonNode root = objectMapper.readTree(jsonResponse);
            if (root.isArray() && !root.isEmpty() && root.get(0).has("total")) {
                return root.get(0).get("total").asDouble();
            }
        } catch (Exception e) {
            System.err.println("Error al procesar consulta de suma para [" + contextLog + "]: " + e.getMessage());
        }
        return 0.0;
    }

    private List<Map<String, Object>> ejecutarConsultaDeRanking(String sql, String contextLog) {
        try {
            String jsonResponse = druidRepository.consultarDatos(sql);
            return objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("Error al procesar consulta de ranking para [" + contextLog + "]: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}