package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.LineItem;
import com.programacion.trabajo_avanzada.repositories.LineItemRepository;
import com.programacion.trabajo_avanzada.repositories.PurchaseOrderRepository;
import com.programacion.trabajo_avanzada.repositories.BookRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LineItemsController implements InjectableController {

    @FXML private TableView<LineItem> tablaItems;
    @FXML private TableColumn<LineItem, Integer> colId;
    @FXML private TableColumn<LineItem, Integer> colOrderId;
    @FXML private TableColumn<LineItem, Integer> colQuantity;
    @FXML private TableColumn<LineItem, String> colIsbn;

    @FXML private TextField txtOrderId;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtIsbn;

    private LineItemRepository lineItemRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private BookRepository bookRepository;

    public LineItemsController() {}

    @Override
    public void injectBeans(ApplicationContext context) {
        this.lineItemRepository = context.getBean(LineItemRepository.class);
        this.purchaseOrderRepository = context.getBean(PurchaseOrderRepository.class);
        this.bookRepository = context.getBean(BookRepository.class);
        initData();
    }

    public void initData() {
        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        colOrderId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getOrderId()));
        colQuantity.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        colIsbn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIsbn()));

        cargarItems();

        tablaItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtOrderId.setText(String.valueOf(newSelection.getOrderId()));
                txtQuantity.setText(String.valueOf(newSelection.getQuantity()));
                txtIsbn.setText(newSelection.getIsbn());
            } else {
                limpiarFormulario();
            }
        });
    }

    private void cargarItems() {
        ObservableList<LineItem> items = FXCollections.observableArrayList(
                StreamSupport.stream(lineItemRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaItems.setItems(items);
    }

    @FXML
    private void guardarItem() {
        try {
            int orderId = Integer.parseInt(txtOrderId.getText());
            String isbn = txtIsbn.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());

            if (!purchaseOrderRepository.existsById((long) orderId)) {
                mostrarError("Error", "La orden con ID " + orderId + " no existe.");
                return;
            }
            if (!bookRepository.existsById(isbn)) {
                mostrarError("Error", "El libro con ISBN " + isbn + " no existe.");
                return;
            }

            LineItem lineItem = LineItem.builder()
                    .orderId(orderId)
                    .quantity(quantity)
                    .isbn(isbn)
                    .build();

            lineItemRepository.save(lineItem);
            cargarItems();
            limpiarFormulario();
            mostrarConfirmacion("Éxito", "El ítem ha sido agregado correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("Error", "Ingrese valores numéricos válidos para Order ID y Cantidad.");
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void editarItem() {
        LineItem seleccionado = tablaItems.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selección inválida", "Seleccione un ítem para editar.");
            return;
        }

        try {
            int orderId = Integer.parseInt(txtOrderId.getText());
            String isbn = txtIsbn.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());

            if (!purchaseOrderRepository.existsById((long) orderId)) {
                mostrarError("Error", "La orden con ID " + orderId + " no existe.");
                return;
            }
            if (!bookRepository.existsById(isbn)) {
                mostrarError("Error", "El libro con ISBN " + isbn + " no existe.");
                return;
            }

            LineItem lineItem = LineItem.builder()
                    .id(seleccionado.getId())
                    .orderId(orderId)
                    .quantity(quantity)
                    .isbn(isbn)
                    .build();

            lineItemRepository.save(lineItem);
            cargarItems();
            limpiarFormulario();
            mostrarConfirmacion("Éxito", "El ítem ha sido editado correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("Error", "Ingrese valores numéricos válidos para Order ID y Quantity.");
        } catch (Exception e) {
            mostrarError("Error al editar", e.getMessage());
        }
    }

    @FXML
    private void eliminarItem() {
        LineItem seleccionado = tablaItems.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea eliminar el ítem seleccionado?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        lineItemRepository.deleteById(seleccionado.getId());
                        cargarItems();
                        limpiarFormulario();
                        mostrarConfirmacion("Éxito", "El ítem ha sido eliminado correctamente.");
                    } catch (Exception e) {
                        mostrarError("Error al eliminar", e.getMessage());
                    }
                }
            });
        } else {
            mostrarError("Selección inválida", "Por favor seleccione un ítem para eliminar.");
        }
    }

    private void limpiarFormulario() {
        txtOrderId.clear();
        txtQuantity.clear();
        txtIsbn.clear();
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
    private void volverMenu(MouseEvent event) {
        try {
            MainApp.mainApp.setScene(Paths.PRINCIPAL_FXML);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo regresar al menú: " + e.getMessage());
        }
    }
}
