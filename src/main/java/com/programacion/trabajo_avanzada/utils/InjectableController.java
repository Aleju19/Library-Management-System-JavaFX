package com.programacion.trabajo_avanzada.utils;

import org.springframework.context.ApplicationContext;

public interface InjectableController {
    void injectBeans(ApplicationContext context);
}

