package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.PurchaseOrder;
import com.programacion.trabajo_avanzada.repositories.PurchaseOrderRepository;
import com.programacion.trabajo_avanzada.repositories.CustomerRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OrdenesController implements InjectableController {

    @FXML
    private TableView<PurchaseOrder> tablaOrdenes;
    @FXML
    private TableColumn<PurchaseOrder, Long> colId;
    @FXML
    private TableColumn<PurchaseOrder, Long> colClienteId;
    @FXML
    private TableColumn<PurchaseOrder, LocalDateTime> colFechaPedido;
    @FXML
    private TableColumn<PurchaseOrder, Integer> colTotal;
    @FXML
    private TableColumn<PurchaseOrder, Integer> colEstado;

    @FXML
    private TextField txtClienteId;
    @FXML
    private TextField txtEstado;
    @FXML
    private TextField txtTotal;

    private PurchaseOrderRepository purchaseOrderRepository;
    private CustomerRepository customerRepository;

    public OrdenesController() {}

    @Override
    public void injectBeans(ApplicationContext context) {
        this.purchaseOrderRepository = context.getBean(PurchaseOrderRepository.class);
        this.customerRepository = context.getBean(CustomerRepository.class);
        initializeData();
    }

    private void initializeData() {
        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        colClienteId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCustomerId()));
        colFechaPedido.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPlacedOn()));
        colEstado.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStatus()));
        colTotal.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotal()));

        cargarOrdenes();

        tablaOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtClienteId.setText(String.valueOf(newSelection.getCustomerId()));
                txtEstado.setText(String.valueOf(newSelection.getStatus()));
                txtTotal.setText(String.valueOf(newSelection.getTotal()));
            } else {
                limpiarFormulario();
            }
        });
    }

    @FXML
    private void cargarOrdenes() {
        ObservableList<PurchaseOrder> ordenes = FXCollections.observableArrayList(
                StreamSupport.stream(purchaseOrderRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaOrdenes.setItems(ordenes);
    }

    @FXML
    private void guardarOrden() {
        if (!validarCampos()) return;

        try {
            long clienteId = Long.parseLong(txtClienteId.getText());
            int estado = Integer.parseInt(txtEstado.getText());
            int total = Integer.parseInt(txtTotal.getText());

            if (!customerRepository.existsById(clienteId)) {
                mostrarError("Error", "El cliente con ID " + clienteId + " no existe.");
                return;
            }

            PurchaseOrder order = PurchaseOrder.builder()
                    .customerId(clienteId)
                    .placedOn(LocalDateTime.now())
                    .deliveredOn(null)
                    .status(estado)
                    .total(total)
                    .build();

            purchaseOrderRepository.save(order);
            cargarOrdenes();
            limpiarFormulario();
            mostrarConfirmacion("Éxito", "La orden ha sido agregada correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("Error", "Ingrese valores numéricos válidos.");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo guardar la orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editarOrden() {
        PurchaseOrder seleccionado = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección requerida", "Debe seleccionar una orden para editar.");
            return;
        }
        if (!validarCampos()) return;

        try {
            long clienteId = Long.parseLong(txtClienteId.getText());
            int estado = Integer.parseInt(txtEstado.getText());
            int total = Integer.parseInt(txtTotal.getText());

            if (!customerRepository.existsById(clienteId)) {
                mostrarError("Error", "El cliente con ID " + clienteId + " no existe.");
                return;
            }

            PurchaseOrder ordenActualizada = PurchaseOrder.builder()
                    .id(seleccionado.getId())
                    .customerId(clienteId)
                    .placedOn(seleccionado.getPlacedOn())
                    .deliveredOn(seleccionado.getDeliveredOn())
                    .status(estado)
                    .total(total)
                    .build();

            purchaseOrderRepository.save(ordenActualizada);
            cargarOrdenes();
            limpiarFormulario();
            mostrarConfirmacion("Éxito", "La orden ha sido actualizada correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("Error", "Ingrese valores numéricos válidos.");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo editar la orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarOrden() {
        PurchaseOrder seleccionado = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección requerida", "Debe seleccionar una orden para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar la orden seleccionada?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    purchaseOrderRepository.deleteById(seleccionado.getId());
                    cargarOrdenes();
                    mostrarConfirmacion("Éxito", "La orden ha sido eliminada correctamente.");
                } catch (Exception e) {
                    Throwable cause = e;
                    boolean handled = false;
                    while (cause != null) {
                        if (cause instanceof DataIntegrityViolationException) {
                            mostrarError("No se puede eliminar la orden",
                                    "Por favor elimine primero los ítems asociados a esta orden.");
                            handled = true;
                            break;
                        }
                        cause = cause.getCause();
                    }
                    if (!handled) {
                        mostrarError("Error al eliminar", e.getMessage());
                    }
                }
            }
        });
    }

    private boolean validarCampos() {
        if (txtClienteId.getText().isEmpty() || txtEstado.getText().isEmpty() || txtTotal.getText().isEmpty()) {
            mostrarError("Campos incompletos", "Por favor, complete todos los campos.");
            return false;
        }
        try {
            Long.parseLong(txtClienteId.getText());
        } catch (NumberFormatException e) {
            mostrarError("Error", "El ID del cliente debe ser un número válido.");
            return false;
        }
        try {
            int estado = Integer.parseInt(txtEstado.getText());
            if (estado != 0 && estado != 1) {
                mostrarError("Error", "El estado solo puede ser 0 o 1.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("Error", "El estado debe ser un número entero 0 (pendiente)  o 1 (entregado).");
            return false;
        }
        try {
            Integer.parseInt(txtTotal.getText());
        } catch (NumberFormatException e) {
            mostrarError("Error", "El total debe ser un número entero válido.");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtClienteId.clear();
        txtEstado.clear();
        txtTotal.clear();
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
