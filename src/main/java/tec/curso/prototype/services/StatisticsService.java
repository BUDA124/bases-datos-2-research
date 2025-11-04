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

    public double obtenerTotalIngresosTaquilla() {
        String sql = "SELECT SUM(\"ingresoBruto\") AS total FROM \"ventas_taquilla\"";
        return ejecutarConsultaDeSuma(sql, "Total Taquilla");
    }

    public double obtenerTotalIngresosDulceria() {
        String sql = "SELECT SUM(\"ingresoBruto\") AS total FROM \"ventas_dulceria\"";
        return ejecutarConsultaDeSuma(sql, "Total Dulcería");
    }

    public List<Map<String, Object>> obtenerRankingPeliculas() {
        String sql = "SELECT \"tituloPelicula\", SUM(\"cantidadTiquetes\") AS total_espectadores " +
                "FROM \"ventas_taquilla\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return ejecutarConsultaDeRanking(sql, "Ranking Películas");
    }

    public List<Map<String, Object>> obtenerRankingProductos() {
        String sql = "SELECT \"nombreProducto\", SUM(\"cantidadVendida\") AS total_vendido " +
                "FROM \"ventas_dulceria\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return ejecutarConsultaDeRanking(sql, "Ranking Productos");
    }

    public List<Map<String, Object>> obtenerIngresosDiariosTaquilla(int dias) {
        String sql = String.format(
                "SELECT TIME_FLOOR(__time, 'P1D') AS dia, SUM(\"ingresoBruto\") AS ingresos " +
                        "FROM \"ventas_taquilla\" " +
                        "WHERE __time >= CURRENT_TIMESTAMP - INTERVAL '%d' DAY " +
                        "GROUP BY 1 ORDER BY 1", dias);
        return ejecutarConsultaDeRanking(sql, "Tendencia Diaria Taquilla");
    }

    public List<Map<String, Object>> obtenerIngresosDiariosDulceria(int dias) {
        String sql = String.format(
                "SELECT TIME_FLOOR(__time, 'P1D') AS dia, SUM(\"ingresoBruto\") AS ingresos " +
                        "FROM \"ventas_dulceria\" " +
                        "WHERE __time >= CURRENT_TIMESTAMP - INTERVAL '%d' DAY " +
                        "GROUP BY 1 ORDER BY 1", dias);
        return ejecutarConsultaDeRanking(sql, "Tendencia Diaria Dulcería");
    }


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

    public String obtenerJsonTotalIngresosTaquilla() {
        String sql = "SELECT SUM(\"ingresoBruto\") AS total FROM \"ventas_taquilla\"";
        return druidRepository.consultarDatos(sql);
    }

    public String obtenerJsonTotalIngresosDulceria() {
        String sql = "SELECT SUM(\"ingresoBruto\") AS total FROM \"ventas_dulceria\"";
        return druidRepository.consultarDatos(sql);
    }

    public String obtenerJsonRankingPeliculas() {
        String sql = "SELECT \"tituloPelicula\", SUM(\"cantidadTiquetes\") AS total_espectadores " +
                "FROM \"ventas_taquilla\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return druidRepository.consultarDatos(sql);
    }

    public String obtenerJsonRankingProductos() {
        String sql = "SELECT \"nombreProducto\", SUM(\"cantidadVendida\") AS total_vendido " +
                "FROM \"ventas_dulceria\" " +
                "GROUP BY 1 ORDER BY 2 DESC LIMIT 5";
        return druidRepository.consultarDatos(sql);
    }
}