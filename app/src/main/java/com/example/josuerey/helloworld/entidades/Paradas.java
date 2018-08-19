package com.example.josuerey.helloworld.entidades;

import java.io.Serializable;

public class Paradas implements Serializable {

    private Integer id_Paradas;
    private Integer id_Rec;
    private Integer hora_ref;
    private Integer t_parada;
    private Integer t_parada2;
    private Integer suben;
    private Integer bajan;
    private Integer p_abordo;
    private Integer coord;

    public Paradas(Integer id_Paradas, Integer id_Rec, Integer hora_ref, Integer t_parada, Integer t_parada2, Integer suben, Integer bajan, Integer p_abordo, Integer coord) {
        this.id_Paradas = id_Paradas;
        this.id_Rec = id_Rec;
        this.hora_ref = hora_ref;
        this.t_parada = t_parada;
        this.t_parada2 = t_parada2;
        this.suben = suben;
        this.bajan = bajan;
        this.p_abordo = p_abordo;
        this.coord = coord;
    }

    public Integer getId_Paradas() {
        return id_Paradas;
    }

    public void setId_Paradas(Integer id_Paradas) {
        this.id_Paradas = id_Paradas;
    }

    public Integer getId_Rec() {
        return id_Rec;
    }

    public void setId_Rec(Integer id_Rec) {
        this.id_Rec = id_Rec;
    }

    public Integer getHora_ref() {
        return hora_ref;
    }

    public void setHora_ref(Integer hora_ref) {
        this.hora_ref = hora_ref;
    }

    public Integer getT_parada() {
        return t_parada;
    }

    public void setT_parada(Integer t_parada) {
        this.t_parada = t_parada;
    }

    public Integer getT_parada2() {
        return t_parada2;
    }

    public void setT_parada2(Integer t_parada2) {
        this.t_parada2 = t_parada2;
    }

    public Integer getSuben() {
        return suben;
    }

    public void setSuben(Integer suben) {
        this.suben = suben;
    }

    public Integer getBajan() {
        return bajan;
    }

    public void setBajan(Integer bajan) {
        this.bajan = bajan;
    }

    public Integer getP_abordo() {
        return p_abordo;
    }

    public void setP_abordo(Integer p_abordo) {
        this.p_abordo = p_abordo;
    }

    public Integer getCoord() {
        return coord;
    }

    public void setCoord(Integer coord) {
        this.coord = coord;
    }
}
