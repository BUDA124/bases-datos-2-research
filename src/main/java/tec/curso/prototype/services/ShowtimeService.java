package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaFuncionDto;
import tec.curso.prototype.repositories.DruidRepository;
import java.time.Instant;

/**
 * Gestiona la lógica de negocio para el módulo "Ocupación y Oportunidad por Función".
 */
@Service
public class ShowtimeService {

    private final DruidRepository druidRepository;

    @Autowired
    public ShowtimeService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Registra la venta de entradas para una función específica, afectando la ocupación.
     * @param venta DTO con los detalles de la función y el número de entradas.
     */
    public void registrarVentaDeEntradas(VentaFuncionDto venta) {
        String sql = String.format(
                "INSERT INTO ocupacion_funciones (__time, sala_nombre, pelicula_titulo, horario_funcion, capacidad_sala, entradas_vendidas) VALUES (TIME_PARSE('%s'), '%s', '%s', '%s', %d, %d)",
                Instant.now().toString(),
                venta.salaNombre(),
                venta.peliculaTitulo(),
                venta.horarioFuncion(),
                venta.capacidadSala(),
                venta.entradasVendidas()
        );
        druidRepository.ingerirDatos(sql);
        System.out.println("Procesadas " + venta.entradasVendidas() + " entradas para la función '" + venta.peliculaTitulo() + "' en la sala " + venta.salaNombre());
    }
}