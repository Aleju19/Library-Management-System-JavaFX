package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.db.BooksAuthor;
import com.programacion.trabajo_avanzada.repositories.AuthorRepository;
import com.programacion.trabajo_avanzada.repositories.BookRepository;
import com.programacion.trabajo_avanzada.repositories.BooksAuthorsRepository;
import com.programacion.trabajo_avanzada.utils.InjectableController;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BooksAuthorsController implements InjectableController {

    @FXML
    private TableColumn<BooksAuthor, Integer> colAutorId;

    @FXML
    private TableColumn<BooksAuthor, String> colIsbn;

    @FXML
    private TableView<BooksAuthor> tablaAutoresLibros;

    @FXML
    private TextField txtAuthorId;

    @FXML
    private TextField txtISBN;

    private BooksAuthorsRepository booksAuthorsRepository;
    private BookRepository bookRepository;       // Asumo que existe
    private AuthorRepository authorRepository;   // Asumo que existe

    @Override
    public void injectBeans(ApplicationContext context) {
        this.booksAuthorsRepository = context.getBean(BooksAuthorsRepository.class);
        this.bookRepository = context.getBean(BookRepository.class);
        this.authorRepository = context.getBean(AuthorRepository.class);
        initialize();
        cargarDatos();
    }

    @FXML
    void initialize() {
        colIsbn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getBooksIsbn()));
        colAutorId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAuthorsId()));
        tablaAutoresLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtISBN.setText(newSelection.getBooksIsbn());
                txtAuthorId.setText(String.valueOf(newSelection.getAuthorsId()));
            }
        });
    }

    void cargarDatos() {
        ObservableList<BooksAuthor> booksAuthors = FXCollections.observableArrayList(
                StreamSupport.stream(booksAuthorsRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
        tablaAutoresLibros.setItems(booksAuthors);
    }

    @FXML
    void eliminarRelacion(ActionEvent event) {
        String isbn = txtISBN.getText().trim();
        String authorIdStr = txtAuthorId.getText().trim();

        if (isbn.isEmpty() || authorIdStr.isEmpty()) {
            mostrarError("Campos vacíos", "Seleccione una fila para eliminar la relación.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("¿Está seguro que desea eliminar esta relación?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // Canceló
        }

        try {
            int authorId = Integer.parseInt(authorIdStr);
            booksAuthorsRepository.deleteRelacion(isbn, authorId);
            cargarDatos();
            limpiarCampos();
            mostrarConfirmacion("Relación eliminada", "La relación libro-autor ha sido eliminada correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("ID inválido", "El ID del autor debe ser un número.");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo eliminar la relación: " + e.getMessage());
        }
    }

    @FXML
    void guardarRelacion(ActionEvent event) {
        String isbn = txtISBN.getText().trim();
        String authorIdStr = txtAuthorId.getText().trim();

        if (isbn.isEmpty() || authorIdStr.isEmpty()) {
            mostrarError("Campos vacíos", "Debe ingresar ISBN y ID del autor.");
            return;
        }

        int authorId;
        try {
            authorId = Integer.parseInt(authorIdStr);
        } catch (NumberFormatException e) {
            mostrarError("ID inválido", "El ID del autor debe ser un número.");
            return;
        }

        // Validar existencia del libro
        if (!bookRepository.existsById(isbn)) {
            mostrarError("ISBN no encontrado", "No existe un libro con el ISBN proporcionado.");
            return;
        }

        // Validar existencia del autor
        if (!authorRepository.existsById((long) authorId)) {
            mostrarError("Autor no encontrado", "No existe un autor con el ID proporcionado.");
            return;
        }

        try {
            BooksAuthor booksAuthor = BooksAuthor.builder()
                    .authorsId(authorId)
                    .booksIsbn(isbn)
                    .build();

            booksAuthorsRepository.insert(booksAuthor.getBooksIsbn(), booksAuthor.getAuthorsId());
            cargarDatos();
            limpiarCampos();
            mostrarConfirmacion("Relación guardada", "La relación libro-autor ha sido guardada correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
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
            mostrarError("Error al volver al menú", e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtISBN.clear();
        txtAuthorId.clear();
    }
}
