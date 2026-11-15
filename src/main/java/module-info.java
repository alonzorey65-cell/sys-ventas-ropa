module pe.edu.upeu {
    // Módulos base de Java y JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires java.net.http;

    // Librerías externas de Maven
    requires org.jsoup;
    requires com.github.librepdf.openpdf;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires com.google.gson;
    requires com.h2database;
    requires lombok;

    // Aperturas para que JavaFX pueda inyectar los @FXML en los controladores
    opens pe.edu.upeu.controller to javafx.fxml;

    // Exportaciones de paquetes para la arquitectura modular
    exports pe.edu.upeu;
    exports pe.edu.upeu.controller;
    exports pe.edu.upeu.model;
    exports pe.edu.upeu.dto;
    exports pe.edu.upeu.dto.comprobante;
    exports pe.edu.upeu.utils;
    exports pe.edu.upeu.components;
    exports pe.edu.upeu.components.comprob;
    exports pe.edu.upeu.config;
    exports pe.edu.upeu.repository;
    exports pe.edu.upeu.service;
    exports pe.edu.upeu.service.impl;
}