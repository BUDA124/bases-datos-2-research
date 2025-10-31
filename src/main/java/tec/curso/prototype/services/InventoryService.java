package tec.curso.prototype.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaProductoDto;
import tec.curso.prototype.repositories.DruidRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Gestiona la lógica de negocio para el módulo "Inventario Inteligente".
 */
@Service
public class InventoryService {

    private final DruidRepository druidRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public InventoryService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Registra la venta de un producto específico del inventario de dulcería.
     * @param venta DTO con el nombre del producto y la cantidad vendida.
     */
    public void registrarVentaDeProducto(VentaProductoDto venta) {
        String sql = String.format(
                "INSERT INTO ventas_productos_inventario (__time, producto_nombre, cantidad_vendida) VALUES (TIME_PARSE('%s'), '%s', %d)",
                Instant.now().toString(),
                venta.producto(),
                venta.cantidad()
        );
        druidRepository.ingerirDatos(sql);
        System.out.println("Procesada venta de " + venta.cantidad() + " unidad(es) de '" + venta.producto() + "'");
    }

    /**
     * **NUEVO MÉTODO**
     * Obtiene una lista de los 5 productos más vendidos, ordenados por la cantidad total vendida.
     *
     * @return Una lista de mapas, donde cada mapa representa un producto y contiene "producto_nombre" y "total_vendido".
     */
    public List<Map<String, Object>> obtenerTop5ProductosVendidos() {
        // Esta consulta agrupa por nombre de producto, suma las cantidades vendidas,
        // ordena de mayor a menor y se queda con los primeros 5 resultados.
        String sql = "SELECT " +
                     "  producto_nombre, " +
                     "  SUM(cantidad_vendida) AS total_vendido " +
                     "FROM ventas_productos_inventario " +
                     "GROUP BY producto_nombre " +
                     "ORDER BY total_vendido DESC " +
                     "LIMIT 5";

        try {
            String jsonResponse = druidRepository.consultarDatos(sql);
            System.out.println("Respuesta de Druid para Top 5 Productos: " + jsonResponse);

            // Jackson convierte directamente el JSON en una lista de mapas, que es muy flexible para la UI.
            return objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String, Object>>>() {});

        } catch (Exception e) {
            System.err.println("Error al consultar o procesar el top 5 de productos: " + e.getMessage());
        }

        // Devuelve una lista vacía en caso de error.
        return Collections.emptyList();
    }
}