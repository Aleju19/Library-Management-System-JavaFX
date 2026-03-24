package com.programacion.trabajo_avanzada.utils.transiciones;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.util.Duration;

public class Animaciones {

    public static void aplicarFadeIn(Parent root) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

}

