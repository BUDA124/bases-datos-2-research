package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaProductoDto;
import tec.curso.prototype.repositories.DruidRepository;
import java.time.Instant;

/**
 * Gestiona la lógica de negocio para el módulo "Inventario Inteligente".
 */
@Service
public class InventoryService {

    private final DruidRepository druidRepository;

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
}