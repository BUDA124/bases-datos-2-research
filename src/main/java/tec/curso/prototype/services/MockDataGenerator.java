package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tec.curso.prototype.dto.ProductSaleEventDto;
import tec.curso.prototype.dto.TicketSaleEventDto;
import tec.curso.prototype.repositories.DruidRepository;
import tec.curso.prototype.store.InMemoryDataStore;
import tec.curso.prototype.store.Pelicula;
import tec.curso.prototype.store.Producto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockDataGenerator implements CommandLineRunner {

    @Value("${prototype.data.generate-on-startup:true}")
    private boolean generateOnStartup;

    @Autowired
    private DruidRepository druidRepository;

    @Autowired
    private InMemoryDataStore dataStore;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (!generateOnStartup) {
            return;
        }


        // Configuración de la simulación
        int diasDeHistorial = 30;
        int eventosPorDia = 500;

        List<Pelicula> peliculasEnCartelera = dataStore.findAllMovies();
        List<Producto> productosDisponibles = dataStore.findAllProducts();

        if (peliculasEnCartelera.isEmpty() || productosDisponibles.isEmpty()) {
            System.err.println("ERROR: No se pueden generar datos de prueba porque no hay películas o productos en el dataStore.");
            return;
        }

        for (int i = 0; i < diasDeHistorial; i++) {
            LocalDateTime diaActual = LocalDateTime.now().minusDays(i);

            for (int j = 0; j < eventosPorDia; j++) {
                // Simula una hora aleatoria del día, con más probabilidad en la tarde/noche
                int hora = simularHoraPico();
                LocalDateTime timestampEvento = diaActual.withHour(hora)
                                                         .withMinute(random.nextInt(60))
                                                         .withSecond(random.nextInt(60));

                if (random.nextBoolean()) {
                    generarVentaDeTiquete(peliculasEnCartelera, timestampEvento.toInstant(ZoneOffset.UTC));
                } else {
                    generarVentaDeProducto(productosDisponibles, timestampEvento.toInstant(ZoneOffset.UTC));
                }
            }
        }
    }

    private void generarVentaDeTiquete(List<Pelicula> peliculas, Instant timestamp) {
        Pelicula pelicula = peliculas.get(random.nextInt(peliculas.size()));

        int cantidad = random.nextInt(4) + 1; // Entre 1 y 4 tiquetes
        TicketSaleEventDto event = new TicketSaleEventDto(timestamp, pelicula.getTitulo(), cantidad, pelicula.getPrecioEntrada());
        druidRepository.ingestTicketSale(event);
    }

    private void generarVentaDeProducto(List<Producto> productos, Instant timestamp) {
        Producto producto = productos.get(random.nextInt(productos.size()));

        int cantidad = random.nextInt(2) + 1; // 1 o 2 productos
        ProductSaleEventDto event = new ProductSaleEventDto(timestamp, producto.getNombre(), cantidad, producto.getPrecio());
        druidRepository.ingestProductSale(event);
    }

    private int simularHoraPico() {
        if (random.nextDouble() < 0.7) {
            return ThreadLocalRandom.current().nextInt(16, 23); // Horas pico (4 PM a 10 PM)
        } else {
            return ThreadLocalRandom.current().nextInt(12, 16); // Horas valle (12 PM a 4 PM)
        }
    }
}