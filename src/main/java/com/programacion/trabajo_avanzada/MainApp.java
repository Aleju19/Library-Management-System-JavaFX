package com.programacion.trabajo_avanzada;

import com.programacion.trabajo_avanzada.config.DBConfig;
import com.programacion.trabajo_avanzada.utils.Paths;
import com.programacion.trabajo_avanzada.utils.SpringFXMLLoader;
import com.programacion.trabajo_avanzada.utils.transiciones.Animaciones;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class MainApp extends Application {

    public static MainApp mainApp;
    private Stage stageWindow;
    private ApplicationContext context;
    private SpringFXMLLoader springFXMLLoader;

    @Override
    public void start(Stage stage) throws Exception {
        mainApp = this;
        stageWindow = stage;

        // Inicializar contexto Spring con tu clase de configuración
        context = new AnnotationConfigApplicationContext(DBConfig.class);

        // Instanciar el loader personalizado con el contexto
        springFXMLLoader = new SpringFXMLLoader(context);

        // Cargar y mostrar la escena inicial
        setScene(Paths.PRINCIPAL_FXML);
    }

    public void setScene(String fxmlPath) throws IOException {
        FXMLLoader loader = springFXMLLoader.load(fxmlPath);
        Parent root = loader.getRoot();

        Animaciones.aplicarFadeIn(root);

        Scene scene = new Scene(root);

        // Si la escena es nueva o diferente, se setea
        if (stageWindow.getScene() == null) {
            stageWindow.setScene(scene);
            stageWindow.show();
        } else {
            stageWindow.setScene(scene);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
