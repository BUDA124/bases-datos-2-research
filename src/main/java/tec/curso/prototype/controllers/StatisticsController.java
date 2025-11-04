package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.StatisticsService;

import java.io.IOException;

@Component
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // Contenedores definidos en el FXML
    @FXML
    private HBox incomeContainer;

    @FXML
    private VBox ticketsIncomeContainer;

    @FXML
    private VBox foodIncomeContainer;

    @FXML
    private VBox movieRankingContainer;

    @FXML
    private VBox foodRankingContainer;

    // Labels dinámicos (para los totales)
    private Label boxOfficeRevenueLabel;
    private Label concessionsRevenueLabel;

    @FXML
    public void initialize() {
        setupIncomeSection();
        setupMovieRankingGraph();
        setupFoodRankingGraph();

        // Datos de ejemplo iniciales
        updateRevenues(15250.50, 8120.00);
        updateMovieRanking();
        updateFoodRanking();
    }

    /** --------------------------- SECCIÓN DE INGRESOS --------------------------- */
    private void setupIncomeSection() {
        // --- Ingresos de taquilla ---
        Label boxOfficeTitle = new Label("Total Box Office Revenue:");
        boxOfficeTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        boxOfficeRevenueLabel = new Label("$0.00");
        boxOfficeRevenueLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: #28a745; -fx-font-weight: bold;");

        ticketsIncomeContainer.getChildren().addAll(boxOfficeTitle, boxOfficeRevenueLabel);
        ticketsIncomeContainer.setAlignment(Pos.CENTER_LEFT);
        ticketsIncomeContainer.setSpacing(10);

        // --- Ingresos de alimentos ---
        Label concessionsTitle = new Label("Total Concessions Revenue:");
        concessionsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        concessionsRevenueLabel = new Label("$0.00");
        concessionsRevenueLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: #007bff; -fx-font-weight: bold;");

        foodIncomeContainer.getChildren().addAll(concessionsTitle, concessionsRevenueLabel);
        foodIncomeContainer.setAlignment(Pos.CENTER_LEFT);
        foodIncomeContainer.setSpacing(10);
    }

    public void updateRevenues(double boxOffice, double concessions) {
        boxOfficeRevenueLabel.setText(String.format("$%,.2f", boxOffice));
        concessionsRevenueLabel.setText(String.format("$%,.2f", concessions));
    }

    /** --------------------------- SECCIÓN GRÁFICO DE PELÍCULAS --------------------------- */
    private BarChart<String, Number> movieRankingGraph;

    private void setupMovieRankingGraph() {
        Label title = new Label("🎬 Movie Ranking");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Películas");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Espectadores");

        movieRankingGraph = new BarChart<>(xAxis, yAxis);
        movieRankingGraph.setPrefHeight(350);
        movieRankingGraph.setPrefWidth(520);
        movieRankingGraph.setLegendVisible(false);

        movieRankingContainer.getChildren().addAll(title, movieRankingGraph);
        movieRankingContainer.setAlignment(Pos.TOP_CENTER);
        movieRankingContainer.setSpacing(10);
    }

    private void updateMovieRanking() {
        movieRankingGraph.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Galaxy Raiders 4", 1200));
        series.getData().add(new XYChart.Data<>("Amores en París", 750));
        series.getData().add(new XYChart.Data<>("La Casa del Terror", 500));

        movieRankingGraph.getData().add(series);

        javafx.application.Platform.runLater(() -> {
            series.getData().get(0).getNode().setStyle("-fx-bar-fill: #ff5f1f;"); // naranja
            series.getData().get(1).getNode().setStyle("-fx-bar-fill: #ffcc00;"); // amarillo
            series.getData().get(2).getNode().setStyle("-fx-bar-fill: #ff0000;"); // rojo
        });

    }

    /** --------------------------- SECCIÓN GRÁFICO DE PRODUCTOS --------------------------- */
    private BarChart<String, Number> foodRankingGraph;

    private void setupFoodRankingGraph() {
        Label title = new Label("🍿 Product Ranking");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Productos");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventas");

        foodRankingGraph = new BarChart<>(xAxis, yAxis);
        foodRankingGraph.setPrefHeight(350);
        foodRankingGraph.setPrefWidth(520);
        foodRankingGraph.setLegendVisible(false);

        foodRankingContainer.getChildren().addAll(title, foodRankingGraph);
        foodRankingContainer.setAlignment(Pos.TOP_CENTER);
        foodRankingContainer.setSpacing(10);
    }

    private void updateFoodRanking() {
        foodRankingGraph.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Palomitas Grandes", 400));
        series.getData().add(new XYChart.Data<>("Hot Dog", 130));
        series.getData().add(new XYChart.Data<>("Refresco Mediano", 120));

        foodRankingGraph.getData().add(series);

        javafx.application.Platform.runLater(() -> {
            series.getData().get(0).getNode().setStyle("-fx-bar-fill: #28a745;"); // verde
            series.getData().get(1).getNode().setStyle("-fx-bar-fill: #17a2b8;"); // celeste
            series.getData().get(2).getNode().setStyle("-fx-bar-fill: #6f42c1;"); // púrpura
        });
    }

    /** --------------------------- MÉTODOS DE NAVEGACIÓN --------------------------- */
    @FXML
    private void registerMovieSale() {
        // TODO: Integrar lógica de ventas de películas
    }

    private void actualizarDatos() {
        // TODO: Cargar desde movieSalesService
    }

    @FXML
    private void action() throws IOException {
        JavaFxApplication.changeScene("SignUp.fxml");
    }
}
