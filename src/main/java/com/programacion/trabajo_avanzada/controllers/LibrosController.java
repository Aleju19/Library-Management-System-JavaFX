package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.Book;
import com.programacion.trabajo_avanzada.repositories.BookRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LibrosController implements InjectableController {

    @FXML
    private TableView<Book> tablaLibros;
    @FXML
    private TableColumn<Book, String> colIsbn;
    @FXML
    private TableColumn<Book, String> colTitulo;
    @FXML
    private TableColumn<Book, Double> colPrecio;

    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtPrecio;

    private BookRepository libroRepository;

    // Guardamos el ISBN seleccionado para detectar cambios
    private String isbnSeleccionado = null;

    public LibrosController() {
        // Constructor vacío requerido por FXMLLoader
    }

    @Override
    public void injectBeans(ApplicationContext context) {
        this.libroRepository = context.getBean(BookRepository.class);
        cargarLibros();
    }

    @FXML
    public void initialize() {
        colIsbn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIsbn()));
        colTitulo.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));
        colPrecio.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));

        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                isbnSeleccionado = newSelection.getIsbn();
                txtIsbn.setText(isbnSeleccionado);
                txtTitulo.setText(newSelection.getTitle());
                txtPrecio.setText(String.valueOf(newSelection.getPrice()));
            }
        });
    }

    private void cargarLibros() {
        if (libroRepository == null) return;

        ObservableList<Book> libros = FXCollections.observableArrayList(
                StreamSupport.stream(libroRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaLibros.setItems(libros);
    }

    private boolean validarTitulo(String titulo) {
        return titulo != null && titulo.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    private boolean validarPrecio(String precio) {
        try {
            Double.parseDouble(precio);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void guardarLibro() {
        String isbn = txtIsbn.getText();
        String titulo = txtTitulo.getText();
        String precioStr = txtPrecio.getText();

        if (isbn == null || isbn.isEmpty()) {
            mostrarError("Error", "El ISBN no puede estar vacío.");
            return;
        }
        if (!validarTitulo(titulo)) {
            mostrarError("Error", "El título solo puede contener letras y espacios.");
            return;
        }
        if (!validarPrecio(precioStr)) {
            mostrarError("Error", "El precio debe ser un número válido.");
            return;
        }

        double precio = Double.parseDouble(precioStr);

        try {
            if (libroRepository.existsById(isbn)) {
                mostrarError("Error", "El ISBN ya existe. Use la opción de editar para modificar.");
                return;
            }

            libroRepository.guardarLibro(isbn, titulo, precio, 1);
            cargarLibros();
            limpiarFormulario();
            mostrarConfirmacion("Libro agregado", "El libro fue agregado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void editarLibro() {
        Book seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección inválida", "Por favor, seleccione un libro para editar.");
            return;
        }

        String isbn = txtIsbn.getText();
        String titulo = txtTitulo.getText();
        String precioStr = txtPrecio.getText();

        if (!validarTitulo(titulo)) {
            mostrarError("Error", "El título solo puede contener letras y espacios.");
            return;
        }
        if (!validarPrecio(precioStr)) {
            mostrarError("Error", "El precio debe ser un número válido.");
            return;
        }

        if (!isbn.equals(isbnSeleccionado)) {
            mostrarError("Error", "No se puede modificar el ISBN de un libro.");
            txtIsbn.setText(isbnSeleccionado);
            return;
        }

        double precio = Double.parseDouble(precioStr);

        try {
            Book book = Book.builder()
                    .isbn(isbn)
                    .title(titulo)
                    .price(precio)
                    .version(1)
                    .build();

            libroRepository.save(book);
            cargarLibros();
            limpiarFormulario();
            mostrarConfirmacion("Libro actualizado", "Se editó correctamente.");
        } catch (Exception e) {
            mostrarError("Error al editar", e.getMessage());
        }
    }

    @FXML
    private void eliminarLibro() {
        Book seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro de que desea eliminar el libro seleccionado?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        libroRepository.deleteById(seleccionado.getIsbn());
                        cargarLibros();
                        limpiarFormulario();
                        mostrarConfirmacion("Libro eliminado", "Se eliminó correctamente.");
                    } catch (Exception e) {
                        mostrarError("Error al eliminar", e.getMessage());
                    }
                }
            });
        } else {
            mostrarError("Selección inválida", "Por favor, seleccione un libro para eliminar.");
        }
    }

    private void limpiarFormulario() {
        txtIsbn.clear();
        txtTitulo.clear();
        txtPrecio.clear();
        isbnSeleccionado = null;
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void volverMenu(MouseEvent event) {
        try {
            MainApp.mainApp.setScene(Paths.PRINCIPAL_FXML);
        } catch (IOException e) {
            mostrarError("Error al volver al menú", e.getMessage());
        }
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
