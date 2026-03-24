package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.Inventory;
import com.programacion.trabajo_avanzada.repositories.BookRepository;
import com.programacion.trabajo_avanzada.repositories.InventoryRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InventarioController implements InjectableController {

    @FXML
    private TableView<Inventory> tablaInventario;
    @FXML
    private TableColumn<Inventory, String> colIsbn;
    @FXML
    private TableColumn<Inventory, Integer> colVendidos;
    @FXML
    private TableColumn<Inventory, Integer> colSuministrados;

    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtVendidos;
    @FXML
    private TextField txtSuministrados;


    private InventoryRepository inventoryRepository;
    private BookRepository bookRepository;


    // Constructor vacío para JavaFX
    public InventarioController() {
    }

    @Override
    public void injectBeans(ApplicationContext context) {
        this.inventoryRepository = context.getBean(InventoryRepository.class);
        this.bookRepository = context.getBean(BookRepository.class);

        initializeData();
    }

    private void initializeData() {
        colIsbn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIsbn()));
        colVendidos.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSold()));
        colSuministrados.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSupplied()));

        cargarInventario();

        tablaInventario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtIsbn.setText(newSelection.getIsbn());
                txtVendidos.setText(String.valueOf(newSelection.getSold()));
                txtSuministrados.setText(String.valueOf(newSelection.getSupplied()));

            } else {
                limpiarFormulario();

            }
        });
    }

    private void cargarInventario() {
        ObservableList<Inventory> inventario = FXCollections.observableArrayList(
                StreamSupport.stream(inventoryRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaInventario.setItems(inventario);
    }

    @FXML
    private void guardarInventario() {
        try {
            if (!validarCampos()) return;

            // Verifica si el libro existe en la base de datos
            if (!bookRepository.existsById(txtIsbn.getText())) {
                mostrarError("ISBN no encontrado", "No existe ningún libro con ese ISBN.");
                return;
            }

            // Verifica que vendidos no sea mayor que suministrados
            int vendidos = Integer.parseInt(txtVendidos.getText());
            int suministrados = Integer.parseInt(txtSuministrados.getText());

            if (vendidos > suministrados) {
                mostrarError("Cantidad inválida", "'Vendidos' no puede ser mayor que 'Suministrados'.");
                return;
            }

            Inventory inventory = Inventory.builder()
                    .isbn(txtIsbn.getText())
                    .sold(vendidos)
                    .supplied(suministrados)
                    .build();

            inventoryRepository.guardarInventario(
                    inventory.getIsbn(),
                    inventory.getSold(),
                    inventory.getSupplied()
            );

            cargarInventario();
            limpiarFormulario();
            mostrarConfirmacion("Operación exitosa", "El inventario ha sido guardado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void editarInventario() {
        Inventory seleccionado = tablaInventario.getSelectionModel().getSelectedItem();

        if (inventoryRepository.existsById(seleccionado.getIsbn())) {
            try {
                Inventory inventory = Inventory.builder()
                        .isbn(txtIsbn.getText())
                        .sold(Integer.valueOf(txtVendidos.getText()))
                        .supplied(Integer.valueOf(txtSuministrados.getText()))
                        .build();

                inventoryRepository.save(inventory);
                cargarInventario();
                limpiarFormulario();
                mostrarConfirmacion("Operación exitosa", "El inventario ha sido actualizado correctamente.");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    private void eliminarInventario() {
        Inventory seleccionado = tablaInventario.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            inventoryRepository.deleteById(seleccionado.getIsbn());
            cargarInventario();
            mostrarConfirmacion("Operación exitosa", "El inventario ha sido eliminado correctamente.");
        }
    }

    private boolean validarCampos() {
        if (txtIsbn.getText().isEmpty() || txtVendidos.getText().isEmpty() || txtSuministrados.getText().isEmpty()) {
            mostrarError("Campos incompletos", "Por favor, complete todos los campos.");
            return false;
        }
        try {
            Integer.parseInt(txtVendidos.getText());
            Integer.parseInt(txtSuministrados.getText());
        } catch (NumberFormatException e) {
            mostrarError("Entrada inválida", "Los campos 'Vendidos' y 'Suministrados' deben ser números válidos.");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtIsbn.clear();
        txtVendidos.clear();
        txtSuministrados.clear();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void volverMenu(MouseEvent event) {
        try {
            MainApp.mainApp.setScene(Paths.PRINCIPAL_FXML);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo regresar al menú: " + e.getMessage());
        }
    }
}
