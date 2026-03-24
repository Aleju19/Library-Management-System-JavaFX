package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.Author;
import com.programacion.trabajo_avanzada.repositories.AuthorRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class AutoresController implements InjectableController {

    @FXML
    private TableView<Author> tablaAutores;
    @FXML
    private TableColumn<Author, Long> colId;
    @FXML
    private TableColumn<Author, String> colNombre;

    @FXML
    private TextField txtNombre;

    private AuthorRepository authorRepository;

    // Constructor vacío obligatorio para JavaFX
    public AutoresController() {
    }

    @Override
    public void injectBeans(ApplicationContext context) {
        this.authorRepository = context.getBean(AuthorRepository.class);
        initData(); // ahora que ya tienes el repositorio, inicializas los datos
    }

    public void initData() {
        if (authorRepository == null) {
            throw new IllegalStateException("AuthorRepository no asignado. Llama a injectBeans primero.");
        }

        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));

        cargarAutores();

        tablaAutores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getName());
            } else {
                limpiarFormulario();
            }
        });
    }

    private void cargarAutores() {
        ObservableList<Author> autores = FXCollections.observableArrayList(
                StreamSupport.stream(authorRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaAutores.setItems(autores);
    }

    @FXML
    private void guardarAutor() {

        try {

            Author author = Author.builder()
                    .name(txtNombre.getText()).version(1)
                    .build();
            authorRepository.save(author);
            cargarAutores();
            limpiarFormulario();
            mostrarConfirmacion("Operación exitosa", "El autor ha sido guardado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void editarAutor() {
        Author seleccionado = tablaAutores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección inválida", "Por favor, seleccione un autor para editar.");
            return;
        }
        if (authorRepository.existsById(seleccionado.getId())) {
            try {
                Author author = Author.builder()
                        .id(seleccionado.getId())
                        .name(txtNombre.getText())
                        .version(seleccionado.getVersion() + 1) // Incrementar la versión
                        .build();
                authorRepository.save(author);
                cargarAutores();
                limpiarFormulario();
                mostrarConfirmacion("Operación exitosa", "El autor ha sido editado correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void eliminarAutor() {
        Author seleccionado = tablaAutores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            authorRepository.deleteById(seleccionado.getId());
            cargarAutores();
            mostrarConfirmacion("Operación exitosa", "El autor ha sido eliminado correctamente.");
        }
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

    private void limpiarFormulario() {
        txtNombre.clear();
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
    void verRelacion(ActionEvent event) {
        try {
            MainApp.mainApp.setScene(Paths.BOOKSAUTHORS_FXML);
        } catch (IOException e) {
            mostrarError("Error al abrir la relación libros-autores", e.getMessage());
        }
    }
}

