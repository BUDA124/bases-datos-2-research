package tec.curso.prototype.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.StatisticsService;
import tec.curso.prototype.store.InMemoryDataStore;
import tec.curso.prototype.store.Producto;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class WelcomeController {

    // Inyección de dependencias de Spring
    private final StatisticsService statisticsService;
    private final InMemoryDataStore dataStore;

    // --- Componentes FXML para las Tarjetas Inteligentes ---
    @FXML
    private Label flujoDineroLabel;
    @FXML
    private Label mejorPeliculaLabel;
    @FXML
    private Label estadoInventarioLabel;

    @Autowired
    public WelcomeController(StatisticsService statisticsService, InMemoryDataStore dataStore) {
        this.statisticsService = statisticsService;
        this.dataStore = dataStore;
    }

    /**
     * Este método es llamado por JavaFX después de que el archivo FXML ha sido cargado.
     * Es ideal para inicializar la carga de datos.
     */
    @FXML
    public void initialize() {
        // Ejecuta la carga de datos en un hilo separado para no bloquear la UI.
        new Thread(this::loadDashboardData).start();
    }

    /**
     * Carga los datos de los servicios y actualiza la UI en el hilo de JavaFX.
     */
    private void loadDashboardData() {
        // 1. Calcular Flujo de Dinero Total
        double totalTaquilla = statisticsService.obtenerTotalIngresosTaquilla();
        double totalDulceria = statisticsService.obtenerTotalIngresosDulceria();
        double flujoTotal = totalTaquilla + totalDulceria;

        // 2. Obtener la Película con Mejor Rendimiento
        List<Map<String, Object>> rankingPeliculas = statisticsService.obtenerRankingPeliculas();
        String peliculaTop;
        if (!rankingPeliculas.isEmpty()) {
            Map<String, Object> top = rankingPeliculas.get(0);
            peliculaTop = String.format("%s (%s espectadores)",
                    top.get("titulo_pelicula"),
                    top.get("total_espectadores"));
        } else {
            peliculaTop = "Sin datos";
        }

        // 3. Verificar el Estado del Inventario
        long itemsBajos = dataStore.findAllProducts().stream()
                .filter(p -> p.getUnidadesDisponibles() <= p.getMinimoSugerido())
                .count();
        String estadoInventario = String.format("%d productos con bajo stock", itemsBajos);

        // Actualiza los componentes FXML en el hilo de la aplicación de JavaFX
        Platform.runLater(() -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            flujoDineroLabel.setText(currencyFormat.format(flujoTotal));
            mejorPeliculaLabel.setText(peliculaTop);
            estadoInventarioLabel.setText(estadoInventario);
        });
    }

    // --- Métodos de Navegación ---

    @FXML
    private void goToSales() throws IOException {
        JavaFxApplication.changeScene("SalesArea.fxml");
    }

    @FXML
    private void goToStatistics() throws IOException {
        JavaFxApplication.changeScene("Statistics.fxml");
    }

    @FXML
    private void goToInventory() throws IOException {
        JavaFxApplication.changeScene("SmartInventory.fxml");
    }
}