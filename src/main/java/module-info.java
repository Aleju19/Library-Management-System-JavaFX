module com.programacion.trabajo_avanzada {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires spring.data.relational;
    requires spring.data.commons;
    requires static lombok;
    requires spring.context;
    requires spring.beans;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires spring.jdbc;
    requires spring.data.jdbc;
    requires spring.tx;

    // Exporta los paquetes que contienen clases públicas

    exports com.programacion.trabajo_avanzada.config;
    exports com.programacion.trabajo_avanzada.controllers;

    exports com.programacion.trabajo_avanzada;
    // Abre paquetes para reflexión
    opens com.programacion.trabajo_avanzada to spring.core, javafx.fxml;
    opens com.programacion.trabajo_avanzada.controllers to javafx.fxml , spring.core;
    opens com.programacion.trabajo_avanzada.db to javafx.base, spring.core , spring.data.relational, spring.data.commons, spring.beans;
    opens com.programacion.trabajo_avanzada.config to javafx.fxml , spring.core, spring.beans;
}
