package tec.curso.prototype.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaDto;
import tec.curso.prototype.repositories.DruidRepository;

import java.time.Instant;

/**
 * Gestiona la lógica de negocio para el módulo "Pulso Financiero del Día".
 * Incluye tanto la ingesta como la consulta de datos financieros.
 */
@Service
public class FinancialService {

    private final DruidRepository druidRepository;

    /**
     * ObjectMapper es una herramienta de la librería Jackson para convertir
     * JSON a objetos Java (y viceversa).
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FinancialService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Registra una venta general (Taquilla o Dulcería) para el pulso financiero.
     * @param venta DTO con el tipo y monto de la venta.
     */
    public void registrarVentaFinanciera(VentaDto venta) {
        String sql = String.format(
                "INSERT INTO ingresos_diarios (__time, fuente_ingreso, monto_venta) VALUES (TIME_PARSE('%s'), '%s', %f)",
                Instant.now().toString(),
                venta.tipo(), // "Taquilla" o "Dulceria"
                venta.monto()
        );
        druidRepository.ingerirDatos(sql);
        System.out.println("Procesada venta financiera de " + venta.tipo() + " por $" + venta.monto());
    }

    /**
     * Consulta a Druid para obtener la suma total de ingresos para una fuente específica.
     *
     * @param fuenteIngreso El tipo de ingreso a consultar, ej: "Taquilla" o "Dulceria".
     * @return Un double con la suma total de los montos de venta. Devuelve 0.0 si no hay datos o hay un error.
     */
    public double obtenerTotalIngresosPorTipo(String fuenteIngreso) {
        //    - "SUM(monto_venta)" para sumar todos los valores de esa columna.
        //    - "AS total" crea un alias para el resultado, que usaremos para encontrar el valor en el JSON.
        //    - "WHERE" filtra los resultados para que solo incluya la fuente que nos interesa.
        String sql = String.format(
                "SELECT SUM(monto_venta) AS total FROM ingresos_diarios WHERE fuente_ingreso = '%s'",
                fuenteIngreso
        );

        try {
            String jsonResponse = druidRepository.consultarDatos(sql);
            System.out.println("Respuesta de Druid para total de '" + fuenteIngreso + "': " + jsonResponse);

            // Parseamos la respuesta JSON.
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.isArray() && !root.isEmpty()) {
                JsonNode primerResultado = root.get(0);

                if (primerResultado.has("total")) {
                    return primerResultado.get("total").asDouble();
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico al consultar o procesar el total de ingresos: " + e.getMessage());
        }
        return 0.0;
    }
}