package com.programacion.trabajo_avanzada.controllers;

import com.programacion.trabajo_avanzada.MainApp;
import com.programacion.trabajo_avanzada.utils.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainController {

    @FXML
    void abrirLibros(ActionEvent event) throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.LIBROS_FXML);
        }catch (IOException e){
            e.printStackTrace();
            // Manejo de error, por ejemplo, mostrar un mensaje al usuario
            System.err.println("Error al abrir la vista de libros: " + e.getMessage());
        }
    }

    @FXML
    void abrirAutores(ActionEvent event) throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.AUTORES_FXML);
        }catch (IOException e){
               e.printStackTrace();
               // Manejo de error, por ejemplo, mostrar un mensaje al usuario
               System.err.println("Error al abrir la vista de autores: " + e.getMessage());
        }
    }

    @FXML
    void abrirClientes(ActionEvent event) throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.CLIENTES_FXML);
        }catch (IOException e){
               e.printStackTrace();
               // Manejo de error, por ejemplo, mostrar un mensaje al usuario
               System.err.println("Error al abrir la vista de clientes: " + e.getMessage());
        }
    }

    @FXML
    void abrirOrdenes(ActionEvent event) throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.ORDENES_FXML);
        } catch (IOException e){
               e.printStackTrace();
               // Manejo de error, por ejemplo, mostrar un mensaje al usuario
               System.err.println("Error al abrir la vista de órdenes: " + e.getMessage());
        }
    }

    @FXML
    void abrirInventario(ActionEvent event)throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.INVENTARIO_FXML);
        }catch (IOException e){
               e.printStackTrace();
               // Manejo de error, por ejemplo, mostrar un mensaje al usuario
               System.err.println("Error al abrir la vista de inventario: " + e.getMessage());
        }
    }

    @FXML
    void abrirLineItems(ActionEvent event)throws IOException {
        try {
            MainApp.mainApp.setScene(Paths.LINEITEMS_FXML);
        }catch (IOException e){
               e.printStackTrace();
               // Manejo de error, por ejemplo, mostrar un mensaje al usuario
               System.err.println("Error al abrir la vista de ítems de línea: " + e.getMessage());
        }
    }

}