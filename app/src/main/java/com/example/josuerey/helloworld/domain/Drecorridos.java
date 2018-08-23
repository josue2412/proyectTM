package com.example.josuerey.helloworld.domain;

import java.io.Serializable;
import java.util.Date;

public class Drecorridos implements Serializable {

    private Integer id_dRec;
    private Integer id_Rec;
    private Integer dhora_ref;
    private Integer dcoord;

    public Drecorridos(Integer id_dRec, Integer id_Rec, Integer dhora_ref, Integer dcoord) {
        this.id_dRec = id_dRec;
        this.id_Rec = id_Rec;
        this.dhora_ref = dhora_ref;
        this.dcoord = dcoord;
    }

    public Integer getId_dRec() {
        return id_dRec;
    }

    public void setId_dRec(Integer id_dRec) {
        this.id_dRec = id_dRec;
    }

    public Integer getId_Rec() {
        return id_Rec;
    }

    public void setId_Rec(Integer id_Rec) {
        this.id_Rec = id_Rec;
    }

    public Integer getDhora_ref() {
        return dhora_ref;
    }

    public void setDhora_ref(Integer dhora_ref) {
        this.dhora_ref = dhora_ref;
    }

    public Integer getDcoord() {
        return dcoord;
    }

    public void setDcoord(Integer dcoord) {
        this.dcoord = dcoord;
    }
}
