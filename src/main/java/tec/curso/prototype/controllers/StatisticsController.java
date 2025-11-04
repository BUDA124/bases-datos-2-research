package tec.curso.prototype.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.StatisticsService;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // --- Componentes FXML ---
    @FXML private VBox ticketsIncomeContainer;
    @FXML private VBox foodIncomeContainer;
    @FXML private VBox movieRankingContainer;
    @FXML private VBox foodRankingContainer;

    // --- Componentes JavaFX creados en código ---
    private Label boxOfficeRevenueLabel;
    private Label concessionsRevenueLabel;
    private BarChart<String, Number> movieRankingGraph;
    private BarChart<String, Number> foodRankingGraph;

    // Colores para los gráficos
    private final String[] CHART_COLORS = {"#ff5f1f", "#ffcc00", "#28a745", "#17a2b8", "#6f42c1"};

    @FXML
    public void initialize() {
        setupIncomeSection();
        setupMovieRankingGraph();
        setupFoodRankingGraph();
        new Thread(this::loadAndDisplayStatistics).start();
    }

    private void loadAndDisplayStatistics() {
        double totalTaquilla = statisticsService.obtenerTotalIngresosTaquilla();
        double totalDulceria = statisticsService.obtenerTotalIngresosDulceria();
        List<Map<String, Object>> rankingPeliculas = statisticsService.obtenerRankingPeliculas();
        List<Map<String, Object>> rankingProductos = statisticsService.obtenerRankingProductos();

        Platform.runLater(() -> {
            updateRevenues(totalTaquilla, totalDulceria);
            updateMovieRanking(rankingPeliculas);
            updateFoodRanking(rankingProductos);
        });
    }

    /** --------------------------- SECCIÓN DE INGRESOS (Sin cambios) --------------------------- */
    private void setupIncomeSection() {
        Label boxOfficeTitle = new Label("Ingresos en Taquilla");
        boxOfficeTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        boxOfficeRevenueLabel = new Label("$0.00");
        boxOfficeRevenueLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        ticketsIncomeContainer.getChildren().addAll(boxOfficeTitle, boxOfficeRevenueLabel);
        ticketsIncomeContainer.setAlignment(Pos.CENTER);
        ticketsIncomeContainer.setSpacing(10);
        ticketsIncomeContainer.setPadding(new Insets(15, 15, 15, 15));

        Label concessionsTitle = new Label("Ingresos en Dulcería");
        concessionsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        concessionsRevenueLabel = new Label("$0.00");
        concessionsRevenueLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: #007bff; -fx-font-weight: bold;");
        foodIncomeContainer.getChildren().addAll(concessionsTitle, concessionsRevenueLabel);
        foodIncomeContainer.setAlignment(Pos.CENTER);
        foodIncomeContainer.setSpacing(10);
        foodIncomeContainer.setPadding(new Insets(15, 15, 15, 15));
    }

    private void updateRevenues(double boxOffice, double concessions) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        boxOfficeRevenueLabel.setText(currencyFormat.format(boxOffice));
        concessionsRevenueLabel.setText(currencyFormat.format(concessions));
    }

    /** --------------------------- GRÁFICO DE PELÍCULAS (Con correcciones) --------------------------- */
    private void setupMovieRankingGraph() {
        Label title = new Label("🎬 Ranking de Películas");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        // ===== INICIO DE CAMBIOS =====
        xAxis.setTickLabelsVisible(false); // Oculta las etiquetas de texto del eje X.
        xAxis.setOpacity(0); // Opcional: Oculta también las marcas del eje.
        // ===== FIN DE CAMBIOS =====

        NumberAxis yAxis = new NumberAxis();
        // ===== INICIO DE CAMBIOS =====
        yAxis.setPrefWidth(40); // Mantiene el ancho fijo para la alineación.
        // Se elimina yAxis.setLabel(...) para ocultar la etiqueta.
        // ===== FIN DE CAMBIOS =====

        movieRankingGraph = new BarChart<>(xAxis, yAxis);
        movieRankingGraph.setLegendVisible(false);
        movieRankingGraph.setBarGap(5);
        movieRankingGraph.setCategoryGap(20);

        movieRankingContainer.getChildren().addAll(title, movieRankingGraph);
        movieRankingContainer.setAlignment(Pos.TOP_CENTER);
        movieRankingContainer.setSpacing(10);
        movieRankingContainer.setPadding(new Insets(15, 15, 15, 15));
        VBox.setVgrow(movieRankingGraph, Priority.ALWAYS);
    }

    /** --------------------------- GRÁFICO DE PRODUCTOS (Con correcciones) --------------------------- */
    private void setupFoodRankingGraph() {
        Label title = new Label("🍿 Ranking de Productos");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        // ===== INICIO DE CAMBIOS =====
        xAxis.setTickLabelsVisible(false); // Oculta las etiquetas de texto del eje X.
        xAxis.setOpacity(0); // Opcional: Oculta también las marcas del eje.
        // ===== FIN DE CAMBIOS =====

        NumberAxis yAxis = new NumberAxis();
        // ===== INICIO DE CAMBIOS =====
        yAxis.setPrefWidth(40); // Mantiene el ancho fijo para la alineación.
        // Se elimina yAxis.setLabel(...) para ocultar la etiqueta.
        // ===== FIN DE CAMBIOS =====

        foodRankingGraph = new BarChart<>(xAxis, yAxis);
        foodRankingGraph.setLegendVisible(false);
        foodRankingGraph.setBarGap(5);
        foodRankingGraph.setCategoryGap(20);

        foodRankingContainer.getChildren().addAll(title, foodRankingGraph);
        foodRankingContainer.setAlignment(Pos.TOP_CENTER);
        foodRankingContainer.setSpacing(10);
        foodRankingContainer.setPadding(new Insets(15, 15, 15, 15));
        VBox.setVgrow(foodRankingGraph, Priority.ALWAYS);
    }

    /** --------------------------- MÉTODOS DE ACTUALIZACIÓN (Sin cambios) --------------------------- */
    private void updateMovieRanking(List<Map<String, Object>> rankingData) {
        movieRankingGraph.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map<String, Object> item : rankingData) {
            String movieTitle = (String) item.get("tituloPelicula");
            Number viewers = (Number) item.get("total_espectadores");
            series.getData().add(new XYChart.Data<>(movieTitle, viewers));
        }

        movieRankingGraph.getData().add(series);

        Platform.runLater(() -> {
            for (int i = 0; i < series.getData().size(); i++) {
                series.getData().get(i).getNode().setStyle("-fx-bar-fill: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
            }
        });
    }

    private void updateFoodRanking(List<Map<String, Object>> rankingData) {
        foodRankingGraph.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map<String, Object> item : rankingData) {
            String productName = (String) item.get("nombreProducto");
            Number totalSold = (Number) item.get("total_vendido");
            series.getData().add(new XYChart.Data<>(productName, totalSold));
        }

        foodRankingGraph.getData().add(series);

        Platform.runLater(() -> {
            for (int i = 0; i < series.getData().size(); i++) {
                series.getData().get(i).getNode().setStyle("-fx-bar-fill: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
            }
        });
    }

    /** --------------------------- MÉTODOS DE NAVEGACIÓN (Sin cambios) --------------------------- */
    @FXML
    private void changeSceneSales(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("SalesArea.fxml");
    }

    @FXML
    private void changeSceneInven(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("SmartInventory.fxml");
    }
}