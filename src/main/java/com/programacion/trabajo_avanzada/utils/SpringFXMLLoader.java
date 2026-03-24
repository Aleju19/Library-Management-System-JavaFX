package com.programacion.trabajo_avanzada.utils;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;

public class SpringFXMLLoader {

    private final ApplicationContext context;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public FXMLLoader load(String fxmlPath) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.load();

        Object controller = loader.getController();

        if (controller instanceof InjectableController) {
            // Inyecta beans y luego llama a metodo initData() para inicialización dependiente de Spring
            ((InjectableController) controller).injectBeans(context);

        }

        return loader;
    }
}
