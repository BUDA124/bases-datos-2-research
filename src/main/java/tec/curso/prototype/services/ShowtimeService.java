package tec.curso.prototype.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaFuncionDto;
import tec.curso.prototype.repositories.DruidRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Gestiona la lógica de negocio para el módulo "Ocupación y Oportunidad por Función".
 */
@Service
public class ShowtimeService {

    private final DruidRepository druidRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ShowtimeService(DruidRepository druidRepository) {
        this.druidRepository = druidRepository;
    }

    /**
     * Registra la venta de entradas para una función específica.
     * @param venta DTO con los detalles de la función y el número de entradas.
     */
    public void registrarVentaDeEntradas(VentaFuncionDto venta) {
        // NOTA: Para un sistema real, la capacidad de la sala no debería insertarse en cada evento.
        // Se haría una sola vez o se obtendría de otra tabla. Para el prototipo, está bien así.
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

    /**
     * **NUEVO MÉTODO**
     * Obtiene el estado de ocupación actual de todas las funciones activas.
     *
     * @return Una lista de mapas, cada uno representando una función con su sala, película, horario,
     *         capacidad y el total de entradas vendidas hasta el momento.
     */
    public List<Map<String, Object>> obtenerOcupacionActualFunciones() {
        // Esta consulta agrupa por los identificadores únicos de una función (sala, película, horario)
        // y suma todas las entradas vendidas para obtener el total de ocupación.
        // MAX(capacidad_sala) se usa porque este valor es constante para la función.
        String sql = "SELECT " +
                     "  sala_nombre, " +
                     "  pelicula_titulo, " +
                     "  horario_funcion, " +
                     "  MAX(capacidad_sala) AS capacidad_sala, " +
                     "  SUM(entradas_vendidas) AS total_vendido " +
                     "FROM ocupacion_funciones " +
                     "GROUP BY sala_nombre, pelicula_titulo, horario_funcion";

        try {
            String jsonResponse = druidRepository.consultarDatos(sql);
            System.out.println("Respuesta de Druid para Ocupación de Funciones: " + jsonResponse);

            return objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String, Object>>>() {});

        } catch (Exception e) {
            System.err.println("Error al consultar la ocupación de las funciones: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}