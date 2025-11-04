package tec.curso.prototype.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmartInventoryController {

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
    @FXML private Button addItemButton;

    // ==== DATA STRUCTURE ====
    private final List<Product> products = new ArrayList<>();

    // Campos dinámicos para update
    private TextField availableFieldUpdate;
    private TextField minFieldUpdate;
    private TextField priceFieldUpdate;

    @FXML
    public void initialize() {
        // Productos iniciales
        products.add(new Product("Palomitas Grandes", 340, 40, 10));
        products.add(new Product("Refresco Mediano", 1000, 100, 4));
        products.add(new Product("Nachos con Queso", 100, 10, 8));
        products.add(new Product("Donas", 100, 10, 8));

        refreshInventoryDisplay();
        refreshChoiceBox();

        // Detectar selección en el ChoiceBox
        updateItemSelect.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                showUpdateFieldsForProduct(newVal);
            }
        });
    }

    // ==== MUESTRA LOS PRODUCTOS EN EL INVENTARIO ====
    private void refreshInventoryDisplay() {
        inventoryContainer.getChildren().clear();

        // 🔹 Asegura que el VBox permita que los hijos se expandan horizontalmente
        inventoryContainer.setFillWidth(true);

        // 🔹 Agrega el título al inicio
        Label title = new Label("List of Items");
        title.setFont(Font.font("Leelawadee UI", 36));
        title.setStyle("-fx-font-weight: bold;");
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE); // Centra el texto
        inventoryContainer.getChildren().add(title);

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);

            HBox row = new HBox();
            row.setSpacing(10);
            row.setPadding(new Insets(10, 20, 10, 20));
            row.setAlignment(Pos.CENTER);
            row.setStyle("-fx-background-color: #31446b; -fx-background-radius: 8;");

            // 🔹 Ajuste automático del ancho
            row.setMaxWidth(Double.MAX_VALUE);

            // 🔹 Esto hace que el HBox crezca dentro del VBox
            VBox.setVgrow(row, javafx.scene.layout.Priority.ALWAYS);

            Label label = new Label(
                    String.format("%d. %s | Unidades Disponibles: %d | Mínimo sugerido: %d | Precio ($): %.2f",
                            i + 1, p.getName(), p.getAvailableUnits(), p.getMinSuggested(), p.getPrice())
            );
            label.setFont(Font.font("Leelawadee UI", 15));
            label.setTextFill(Color.WHITE);

            row.getChildren().add(label);
            inventoryContainer.getChildren().add(row);
        }
    }


    // ==== ACTUALIZA LOS ITEMS DEL CHOICEBOX ====
    private void refreshChoiceBox() {
        List<String> names = new ArrayList<>();
        for (Product p : products) {
            names.add(p.getName());
        }
        updateItemSelect.setItems(FXCollections.observableArrayList(names));
    }

    // ==== MUESTRA CAMPOS PARA EDITAR UN PRODUCTO ====
    private void showUpdateFieldsForProduct(String productName) {
        // Elimina los HBox antiguos antes del botón
        updateProductContainer.getChildren().removeIf(node -> node instanceof HBox && node != updateItemButton.getParent());

        Product selected = findProductByName(productName);
        if (selected == null) return;

        availableFieldUpdate = createEditableField("Unidades Disponibles:", String.valueOf(selected.getAvailableUnits()));
        minFieldUpdate = createEditableField("Mínimo Sugerido:", String.valueOf(selected.getMinSuggested()));
        priceFieldUpdate = createEditableField("Precio ($):", String.valueOf(selected.getPrice()));

        // Inserta los nuevos campos antes del botón
        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, availableFieldUpdate.getParent());
        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, minFieldUpdate.getParent());
        updateProductContainer.getChildren().add(updateProductContainer.getChildren().size() - 1, priceFieldUpdate.getParent());
    }

    private TextField createEditableField(String labelText, String value) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10, 20, 10, 20));

        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Leelawadee UI Bold", 18));

        TextField field = new TextField(value);
        field.setPrefWidth(150);

        hbox.getChildren().addAll(label, field);
        return field;
    }

    // ==== BOTÓN "ACTUALIZAR PRODUCTO" ====
    @FXML
    public void updateItem(ActionEvent event) {
        String selectedName = updateItemSelect.getValue();
        if (selectedName == null) {
            showAlert("Seleccione un producto para actualizar.");
            return;
        }

        Product p = findProductByName(selectedName);
        if (p == null) return;

        try {
            p.setAvailableUnits(Integer.parseInt(availableFieldUpdate.getText()));
            p.setMinSuggested(Integer.parseInt(minFieldUpdate.getText()));
            p.setPrice(Double.parseDouble(priceFieldUpdate.getText()));
        } catch (NumberFormatException e) {
            showAlert("Por favor ingrese valores válidos (numéricos).");
            return;
        }

        showAlert("Producto actualizado exitosamente ✅");
        refreshInventoryDisplay();

        // 🔹 Limpia los campos de actualización, pero conserva ChoiceBox y botón
        updateProductContainer.getChildren().clear();

        // 🔹 Recrea los elementos iniciales del panel
        Label titleLabel = new Label("Update Item");
        titleLabel.setFont(Font.font("Leelawadee UI Bold", 36));
        titleLabel.setTextFill(Color.WHITE);

        updateProductContainer.getChildren().add(titleLabel);
        updateProductContainer.getChildren().add(updateItemSelect);
        updateProductContainer.getChildren().add(updateItemButton);

        // 🔹 Limpia la selección del ChoiceBox (para que se pueda volver a usar)
        updateItemSelect.getSelectionModel().clearSelection();
    }


    // ==== BOTÓN "AGREGAR PRODUCTO" ====
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

            products.add(new Product(name, available, min, price));
            refreshInventoryDisplay();
            refreshChoiceBox();

            // Limpia los campos
            productNameField.clear();
            availabilityField.clear();
            minField.clear();
            priceField.clear();

            showAlert("Producto agregado correctamente ✅");
        } catch (NumberFormatException e) {
            showAlert("Por favor ingrese valores numéricos válidos.");
        }
    }

    private Product findProductByName(String name) {
        return products.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Smart Inventory");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==== CLASE PRODUCTO INTERNA ====
    private static class Product {
        private String name;
        private int availableUnits;
        private int minSuggested;
        private double price;

        public Product(String name, int availableUnits, int minSuggested, double price) {
            this.name = name;
            this.availableUnits = availableUnits;
            this.minSuggested = minSuggested;
            this.price = price;
        }

        public String getName() { return name; }
        public int getAvailableUnits() { return availableUnits; }
        public int getMinSuggested() { return minSuggested; }
        public double getPrice() { return price; }

        public void setAvailableUnits(int availableUnits) { this.availableUnits = availableUnits; }
        public void setMinSuggested(int minSuggested) { this.minSuggested = minSuggested; }
        public void setPrice(double price) { this.price = price; }
    }
}
