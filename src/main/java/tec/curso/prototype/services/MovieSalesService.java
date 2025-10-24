package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.VentaPeliculaDto;
import tec.curso.prototype.repositories.DruidRepository;
import java.time.Instant;

/**
 * Gestiona la lógica de negocio para el módulo "El Poder de Venta de las Películas".
 */
@Service
public class MovieSalesService {

    private final DruidRepository druidRepository;

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
}