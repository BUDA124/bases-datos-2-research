package tec.curso.prototype.controllers;

import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.store.InMemoryDataStore;
import tec.curso.prototype.store.Producto;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SmartInventoryController {

    @Autowired
    private InMemoryDataStore dataStore;

    // ==== FXML ELEMENTS ====
    @FXML private VBox inventoryContainer;
    @FXML private VBox updateProductContainer;
    @FXML private ChoiceBox<String> updateItemSelect;
    @FXML private Button updateItemButton;
    @FXML private VBox createProductContainer;
    @FXML private TextField productNameField;
    @FXML private TextField availabilityField;
    @FXML private TextField minField;
    @FXML private TextField priceField;

    // Campos dinámicos para update
    private TextField availableFieldUpdate;
    private TextField minFieldUpdate;
    private TextField priceFieldUpdate;

    @FXML
    public void initialize() {
        // Carga los datos desde el almacén central al iniciar
        refreshInventoryDisplay();
        refreshChoiceBox();

        updateItemSelect.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                showUpdateFieldsForProduct(newVal);
            }
        });
    }

    private void refreshInventoryDisplay() {
        inventoryContainer.getChildren().clear();
        inventoryContainer.setFillWidth(true);

        Label title = new Label("List of Items");
        title.setFont(Font.font("Leelawadee UI", 36));
        title.setStyle("-fx-font-weight: bold;");
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setWrapText(true); // MODIFICACIÓN: Permite que el texto del título se ajuste si es necesario.
        inventoryContainer.getChildren().add(title);

        List<Producto> liveProducts = dataStore.findAllProducts();

        for (int i = 0; i < liveProducts.size(); i++) {
            Producto p = liveProducts.get(i);
            HBox row = new HBox(10);
            row.setSpacing(10);
            row.setPadding(new Insets(10, 20, 10, 20));
            row.setAlignment(Pos.CENTER);
            row.setStyle("-fx-background-color: #31446b; -fx-background-radius: 8;");
            row.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(row, javafx.scene.layout.Priority.ALWAYS);

            Label label = new Label(
                    String.format("%d. %s | Unidades Disponibles: %d | Mínimo sugerido: %d | Precio ($): %.2f",
                            i + 1, p.getNombre(), p.getUnidadesDisponibles(), p.getMinimoSugerido(), p.getPrecio())
            );
            label.setFont(Font.font("Leelawadee UI", 15));
            label.setTextFill(Color.WHITE);
            label.setWrapText(true); // MODIFICACIÓN: Permite que el texto de la fila se ajuste para no desbordar la pantalla.

            row.getChildren().add(label);
            inventoryContainer.getChildren().add(row);
        }
    }

    private void refreshChoiceBox() {
        List<String> names = dataStore.findAllProducts().stream()
                .map(Producto::getNombre)
                .collect(Collectors.toList());
        updateItemSelect.setItems(FXCollections.observableArrayList(names));
    }

    private void showUpdateFieldsForProduct(String productName) {
        clearUpdateFields(); // Limpia campos anteriores
        Producto selected = dataStore.findProductByName(productName);
        if (selected == null) return;

        availableFieldUpdate = createEditableField("Unidades Disponibles:", String.valueOf(selected.getUnidadesDisponibles()));
        minFieldUpdate = createEditableField("Mínimo Sugerido:", String.valueOf(selected.getMinimoSugerido()));
        priceFieldUpdate = createEditableField("Precio ($):", String.valueOf(selected.getPrecio()));

        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, availableFieldUpdate.getParent());
        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, minFieldUpdate.getParent());
        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, priceFieldUpdate.getParent());
    }

    @FXML
    public void updateItem(ActionEvent event) {
        String selectedName = updateItemSelect.getValue();
        if (selectedName == null) {
            showAlert("Seleccione un producto para actualizar.");
            return;
        }

        Producto p = dataStore.findProductByName(selectedName);
        if (p == null) return;

        try {
            p.setUnidadesDisponibles(Integer.parseInt(availableFieldUpdate.getText()));
            p.setMinimoSugerido(Integer.parseInt(minFieldUpdate.getText()));
            p.setPrecio(Double.parseDouble(priceFieldUpdate.getText()));

            dataStore.updadteExistingProduct(p);

        } catch (NumberFormatException e) {
            showAlert("Por favor ingrese valores válidos (numéricos).");
            return;
        }

        showAlert("Producto actualizado exitosamente ✅");
        refreshInventoryDisplay();
        refreshChoiceBox();
        clearUpdateFields();
    }

    @FXML
    public void addItem(ActionEvent event) {
        try {
            String name = productNameField.getText();
            int available = Integer.parseInt(availabilityField.getText());
            int min = Integer.parseInt(minField.getText());
            double price = Double.parseDouble(priceField.getText());

            if (name.isBlank()) {
                showAlert("El nombre del producto no puede estar vacío.");
                return;
            }

            Producto newProduct = new Producto(name, available, min, price);
            dataStore.createNewProduct(newProduct);

            refreshInventoryDisplay();
            refreshChoiceBox();

            productNameField.clear();
            availabilityField.clear();
            minField.clear();
            priceField.clear();

            showAlert("Producto agregado correctamente ✅");
        } catch (NumberFormatException e) {
            showAlert("Por favor ingrese valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }
    }

    // --- Métodos de utilidad (helper) ---
    private void clearUpdateFields() {
        updateProductContainer.getChildren().removeIf(node -> node instanceof HBox && node != updateItemButton.getParent());
        updateItemSelect.getSelectionModel().clearSelection();
    }

    private TextField createEditableField(String labelText, String value) {
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10, 20, 10, 20));
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Leelawadee UI Bold", 18));
        label.setWrapText(true); // MODIFICACIÓN: Permite que el texto de la etiqueta se ajuste.
        TextField field = new TextField(value);
        field.setPrefWidth(150);
        hbox.getChildren().addAll(label, field);
        return field;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Smart Inventory");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void changeSceneStats(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("Statistics.fxml");
    }

    @FXML
    private void changeSceneSales(MouseEvent event) throws IOException {
        JavaFxApplication.changeScene("SalesArea.fxml");
    }
}