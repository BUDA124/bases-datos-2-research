package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaDto;
import tec.curso.prototype.repositories.DruidRepository;
import java.time.Instant;

/**
 * Gestiona la lógica de negocio para el módulo "Pulso Financiero del Día".
 */
@Service
public class FinancialService {

    private final DruidRepository druidRepository;

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
}