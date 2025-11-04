package tec.curso.prototype.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
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
    @Autowired
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (!generateOnStartup) {
            System.out.println("La generación de datos de prueba está deshabilitada.");
            return;
        }

        System.out.println("Iniciando la generación de datos de prueba para Druid...");

        // Configuración de la simulación
        int diasDeHistorial = 30;
        int eventosPorDia = 500;

        List<Pelicula> peliculasEnCartelera = dataStore.findAllMovies();
        List<Producto> productosDisponibles = dataStore.findAllProducts();

        if (peliculasEnCartelera.isEmpty() || productosDisponibles.isEmpty()) {
            System.err.println("ERROR: No se pueden generar datos. No hay películas o productos en el dataStore.");
            return;
        }

        List<TicketSaleEventDto> eventosTiquetes = new ArrayList<>();
        List<ProductSaleEventDto> eventosProductos = new ArrayList<>();

        for (int i = 0; i < diasDeHistorial; i++) {
            LocalDateTime diaActual = LocalDateTime.now().minusDays(i);
            for (int j = 0; j < eventosPorDia; j++) {
                int hora = simularHoraPico();
                LocalDateTime timestampEvento = diaActual.withHour(hora)
                        .withMinute(random.nextInt(60))
                        .withSecond(random.nextInt(60));
                Instant timestamp = timestampEvento.toInstant(ZoneOffset.UTC);

                if (random.nextBoolean()) {
                    Pelicula pelicula = peliculasEnCartelera.get(random.nextInt(peliculasEnCartelera.size()));
                    int cantidad = random.nextInt(4) + 1;
                    eventosTiquetes.add(new TicketSaleEventDto(timestamp, pelicula.getTitulo(), cantidad, pelicula.getPrecioEntrada()));
                } else {
                    Producto producto = productosDisponibles.get(random.nextInt(productosDisponibles.size()));
                    int cantidad = random.nextInt(2) + 1;
                    eventosProductos.add(new ProductSaleEventDto(timestamp, producto.getNombre(), cantidad, producto.getPrecio()));
                }
            }
        }
        System.out.println("Generados " + eventosTiquetes.size() + " eventos de tiquetes y " + eventosProductos.size() + " eventos de productos.");

        String ticketTaskId = null;
        if (!eventosTiquetes.isEmpty()) {
            ticketTaskId = ingestarLoteTiquetes(eventosTiquetes);
        }

        String productTaskId = null;
        if (!eventosProductos.isEmpty()) {
            productTaskId = ingestarLoteProductos(eventosProductos);
        }

        System.out.println("Tareas de ingesta por lotes enviadas a Druid. Revisa la consola de Druid en http://localhost:8888/unified-console.html#tasks");
        System.out.println("Esperando a que las tareas de ingesta de Druid terminen...");
        waitForTaskCompletion(ticketTaskId);
        waitForTaskCompletion(productTaskId);

        System.out.println("¡Todas las tareas de ingesta han terminado! La aplicación continuará.");
    }

    private String ingestarLoteTiquetes(List<TicketSaleEventDto> eventos) throws Exception {
        StringBuilder dataBuilder = new StringBuilder();
        for (TicketSaleEventDto evento : eventos) {
            dataBuilder.append(objectMapper.writeValueAsString(evento)).append("\n");
        }
        String inlineData = dataBuilder.toString();

        String specJson = """
        {
          "type": "index_parallel",
          "spec": {
            "dataSchema": {
              "dataSource": "ventas_taquilla",
              "timestampSpec": {
                "column": "timestamp",
                "format": "iso"
              },
              "dimensionsSpec": {
                "dimensions": ["tituloPelicula"]
              },
              "metricsSpec": [
                { "type": "longSum", "name": "cantidadTiquetes", "fieldName": "cantidadTiquetes" },
                { "type": "doubleSum", "name": "precioUnitario", "fieldName": "precioUnitario" },
                { "type": "doubleSum", "name": "ingresoBruto", "fieldName": "ingresoBruto" }
              ],
              "granularitySpec": { "type": "uniform", "segmentGranularity": "DAY", "queryGranularity": "NONE", "rollup": true }
            },
            "ioConfig": {
              "type": "index_parallel",
              "inputSource": {
                "type": "inline",
                "data": %s
              },
              "inputFormat": { "type": "json" }
            },
            "tuningConfig": { "type": "index_parallel", "partitionsSpec": { "type": "dynamic" } }
          }
        }
        """.formatted(objectMapper.writeValueAsString(inlineData));

        return druidRepository.submitBatchIngestionTask(specJson);
    }

    private String ingestarLoteProductos(List<ProductSaleEventDto> eventos) throws Exception {
        StringBuilder dataBuilder = new StringBuilder();
        for (ProductSaleEventDto evento : eventos) {
            dataBuilder.append(objectMapper.writeValueAsString(evento)).append("\n");
        }
        String inlineData = dataBuilder.toString();

        String specJson = """
        {
          "type": "index_parallel",
          "spec": {
            "dataSchema": {
              "dataSource": "ventas_dulceria",
              "timestampSpec": { "column": "timestamp", "format": "iso" },
              "dimensionsSpec": { "dimensions": ["nombreProducto"] },
              "metricsSpec": [
                { "type": "longSum", "name": "cantidadVendida", "fieldName": "cantidadVendida" },
                { "type": "doubleSum", "name": "precioUnitario", "fieldName": "precioUnitario" },
                { "type": "doubleSum", "name": "ingresoBruto", "fieldName": "ingresoBruto" }
              ],
              "granularitySpec": { "type": "uniform", "segmentGranularity": "DAY", "queryGranularity": "NONE", "rollup": true }
            },
            "ioConfig": {
              "type": "index_parallel",
              "inputSource": { "type": "inline", "data": %s },
              "inputFormat": { "type": "json" }
            },
            "tuningConfig": { "type": "index_parallel", "partitionsSpec": { "type": "dynamic" } }
          }
        }
        """.formatted(objectMapper.writeValueAsString(inlineData));

        return druidRepository.submitBatchIngestionTask(specJson);
    }

    private void waitForTaskCompletion(String taskId) throws InterruptedException {
        if (taskId == null) return;

        long startTime = System.currentTimeMillis();
        long timeout = 300_000; // 5 minutos de tiempo de espera máximo

        while (System.currentTimeMillis() - startTime < timeout) {
            String status = druidRepository.getTaskStatus(taskId);
            System.out.println("Estado de la tarea " + taskId + ": " + status);

            if ("SUCCESS".equalsIgnoreCase(status)) {
                System.out.println("Tarea " + taskId + " completada con éxito.");
                return;
            }

            if ("FAILED".equalsIgnoreCase(status) || "UNKNOWN".equalsIgnoreCase(status)) {
                System.err.println("La tarea " + taskId + " ha fallado o está en un estado desconocido.");
                return;
            }

            // Espera 5 segundos antes de volver a preguntar
            Thread.sleep(5000);
        }

        System.err.println("Tiempo de espera agotado para la tarea " + taskId);
        // throw new RuntimeException("Tiempo de espera agotado para la tarea de ingesta " + taskId);
    }

    private int simularHoraPico() {
        if (random.nextDouble() < 0.7) {
            return ThreadLocalRandom.current().nextInt(16, 23); // Horas pico
        } else {
            return ThreadLocalRandom.current().nextInt(12, 16); // Horas valle
        }
    }
}