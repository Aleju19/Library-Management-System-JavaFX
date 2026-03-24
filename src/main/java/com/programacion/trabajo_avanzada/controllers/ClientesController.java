package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.Customer;
import com.programacion.trabajo_avanzada.repositories.CustomerRepository;
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

public class ClientesController implements InjectableController {

    @FXML private TableView<Customer> tablaClientes;
    @FXML private TableColumn<Customer, Long> colId;
    @FXML private TableColumn<Customer, String> colNombre;
    @FXML private TableColumn<Customer, String> colEmail;

    //@FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;

    private CustomerRepository customerRepository;

    public ClientesController() {
        // Constructor vacío requerido por JavaFX
    }

    @Override
    public void injectBeans(ApplicationContext context) {
        this.customerRepository = context.getBean(CustomerRepository.class);
        initData();
    }

    @FXML
    public void initialize() {
        // Configura columnas y listeners sin usar el repositorio
        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getName());
                txtEmail.setText(newSelection.getEmail());
            } else {
                limpiarFormulario();
            }
        });
    }

    public void initData() {
        if (customerRepository == null) {
            throw new IllegalStateException("CustomerRepository no asignado. Llama a injectBeans antes de usar initData.");
        }
        cargarClientes();
    }

    private void cargarClientes() {
        ObservableList<Customer> clientes = FXCollections.observableArrayList(
                StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaClientes.setItems(clientes);
    }

    private boolean validarNombre(String nombre) {
        return nombre != null && nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    private boolean validarEmail(String email) {
        if (email == null) return false;
        return email.matches("^[\\w.-]+@(gmail\\.com|outlook\\.com|yahoo\\.com|uce\\.edu\\.ec)$");
    }

    @FXML
    private void guardarCliente() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();

        if (!validarNombre(nombre)) {
            mostrarError("Nombre inválido", "El nombre solo puede contener letras y espacios.");
            return;
        }
        if (!validarEmail(email)) {
            mostrarError("Email inválido", "El email debe terminar en @gmail.com, @outlook.com, @yahoo.com o @uce.edu.ec");
            return;
        }

        try {
            Customer customer = Customer.builder().name(nombre).email(email).version(1).build();
            customerRepository.save(customer);
            cargarClientes();
            limpiarFormulario();
            mostrarConfirmacion("Operación exitosa", "El cliente ha sido agregado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void editarCliente() {
        Customer seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección inválida", "Por favor, seleccione un cliente para editar.");
            return;
        }

        String nombre = txtNombre.getText();
        String email = txtEmail.getText();

        if (!validarNombre(nombre)) {
            mostrarError("Nombre inválido", "El nombre solo puede contener letras y espacios.");
            return;
        }
        if (!validarEmail(email)) {
            mostrarError("Email inválido", "El email debe terminar en @gmail.com, @outlook.com, @yahoo.com o @uce.edu.ec");
            return;
        }

        try {
            seleccionado.setName(nombre);
            seleccionado.setEmail(email);
            customerRepository.save(seleccionado);
            cargarClientes();
            tablaClientes.refresh(); // Fuerza refresco visual
            limpiarFormulario();
            mostrarConfirmacion("Operación exitosa", "El cliente ha sido editado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al editar", e.getMessage());
        }
    }

    @FXML
    private void eliminarCliente() {
        Customer seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText(null);
            alert.setContentText("¿Está seguro de que desea eliminar al cliente seleccionado?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        customerRepository.deleteById(seleccionado.getId());
                        cargarClientes();
                        mostrarConfirmacion("Operación exitosa", "El cliente ha sido eliminado correctamente.");
                    } catch (Exception e) {
                        mostrarError("Error al eliminar", e.getMessage());
                    }
                }
            });
        } else {
            mostrarError("Selección inválida", "Por favor, seleccione un cliente para eliminar.");
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtEmail.clear();
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
