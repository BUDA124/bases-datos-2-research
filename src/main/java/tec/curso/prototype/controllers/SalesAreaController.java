package tec.curso.prototype.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.store.InMemoryDataStore;
import tec.curso.prototype.services.SalesAreaService;
import tec.curso.prototype.store.Pelicula;
import tec.curso.prototype.store.Producto;

import java.io.IOException;
import java.util.List;

@Component
public class SalesAreaController {

    @Autowired
    private SalesAreaService salesAreaService;
    @Autowired
    private InMemoryDataStore dataStore;

    @FXML private VBox ticketsContainer;
    @FXML private VBox foodContainer;

    @FXML
    private Text TextStatistics;
    @FXML
    private Text TextInventory;

    @FXML
    public void initialize() {
        // Carga los productos y películas desde el almacén central
        List<Pelicula> peliculas = dataStore.findAllMovies();
        List<Producto> productos = dataStore.findAllProducts();

        // Cargar los ítems dinámicamente
        loadMovieItems(ticketsContainer, peliculas);
        loadProductItems(foodContainer, productos);
    }

    private void loadMovieItems(VBox container, List<Pelicula> items) {
        setupContainer(container);
        for (Pelicula item : items) {
            container.getChildren().add(createItemRow(item.getTitulo()));
        }
    }

    private void loadProductItems(VBox container, List<Producto> items) {
        setupContainer(container);
        for (Producto item : items) {
            container.getChildren().add(createItemRow(item.getNombre()));
        }
    }

    private void setupContainer(VBox container) {
        container.getChildren().clear();
        container.setFillWidth(false);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);
    }

    private HBox createItemRow(String itemName) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 5;"); // Padding aumentado de 5 a 10
        row.setPrefWidth(450); // Ancho aumentado de 400 a 450

        Label name = new Label(itemName);
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;"); // Tamaño de fuente aumentado de 16px a 18px

        Spinner<Integer> spinner = new Spinner<>(0, 10, 0);
        spinner.setPrefSize(100, 40); // Se define un tamaño preferido para el control

        row.getChildren().addAll(name, spinner);
        return row;
    }

    @FXML
    private void registerSale(ActionEvent event) {
        if (!hasSelectedItems(ticketsContainer) && !hasSelectedItems(foodContainer)) {
            showNotification(event, "No Sale", "Please select at least one item.", false);
            return;
        }

        // Llama a la lógica de negocio para procesar las ventas
        processSales(ticketsContainer, "TICKET");
        processSales(foodContainer, "PRODUCT");

        showNotification(event, "Sale Registered", "The new sale has been added successfully.", true);

        clearSpinners(ticketsContainer);
        clearSpinners(foodContainer);
    }

    private void processSales(VBox container, String saleType) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Label nameLabel = (Label) row.getChildren().get(0);
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);
                int quantity = spinner.getValue();

                if (quantity > 0) {
                    String itemName = nameLabel.getText();
                    boolean success;

                    if ("TICKET".equals(saleType)) {
                        success = salesAreaService.registrarVentaTiquete(itemName, quantity);
                    } else { // "PRODUCT"
                        success = salesAreaService.registrarVentaProducto(itemName, quantity);
                    }

                    if (success) {
                        System.out.println("Venta registrada: " + quantity + " de " + itemName);
                    } else {
                        System.err.println("Falló la venta de: " + quantity + " de " + itemName + " (no encontrado o sin stock)");
                    }
                }
            }
        }
    }

    // --- Métodos de utilidad (helper) ---
    private void showNotification(ActionEvent event, String title, String text, boolean isConfirm) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(text)
                .position(Pos.BOTTOM_LEFT)
                .hideAfter(Duration.seconds(5))
                .owner(getStageFromEvent(event));

        if (isConfirm) {
            notification.showConfirm();
        } else {
            notification.showWarning();
        }
    }

    private void clearSpinners(VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);
                spinner.getValueFactory().setValue(0);
            }
        }
    }

    private boolean hasSelectedItems(VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);
                if (spinner.getValue() > 0) return true;
            }
        }
        return false;
    }

    private Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    @FXML
    private void changeSceneStats(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("Statistics.fxml");
    }

    @FXML
    private void changeSceneInven(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("SmartInventory.fxml");
    }
}