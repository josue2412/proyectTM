package com.example.josuerey.helloworld.domain;

import java.io.Serializable;

public class Recorrido implements Serializable {

    private Integer id;
    private String nom_ruta;
    private String via;
    private String num_econ;
    private String encuestador;


    public Recorrido() {
        this.id = id;
        this.nom_ruta = nom_ruta;
        this.via = via;
        this.num_econ = num_econ;
        this.encuestador = encuestador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom_ruta() {
        return nom_ruta;
    }

    public void setNom_ruta(String nom_ruta) {
        this.nom_ruta = nom_ruta;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getNum_econ() {
        return num_econ;
    }

    public void setNum_econ(String num_econ) {
        this.num_econ = num_econ;
    }

    public String getEncuestador() {
        return encuestador;
    }

    public void setEncuestador(String encuestador) {
        this.encuestador = encuestador;
    }
}
