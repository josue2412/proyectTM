package com.example.josuerey.helloworld.preloaddata;

import android.support.annotation.NonNull;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;

public class ViaOfStudyData {
    @NonNull
    public static ViaOfStudy[] getBusRouteData() {
        return new ViaOfStudy[] {
                ViaOfStudy.builder().id(1).via("Ant. Carr. a Chapala").build(),
                ViaOfStudy.builder().id(2).via("Antiguo Camino a Santa Cruz del Valle").build(),
                ViaOfStudy.builder().id(3).via("Av. Adolf B. Horn Jr.").build(),
                ViaOfStudy.builder().id(4).via("Av. Adolfo López Mateos").build(),
                ViaOfStudy.builder().id(5).via("Calerilla").build(),
                ViaOfStudy.builder().id(6).via("Carr. A Chapala Y Lázaro Cárdenas").build(),
                ViaOfStudy.builder().id(7).via("Periférico y Colón").build(),
                ViaOfStudy.builder().id(8).via("Periférico y Juan de La Barrera").build(),
                ViaOfStudy.builder().id(9).via("Prol. 8 de Julio").build(),
                ViaOfStudy.builder().id(10).via("Prol. Colón").build()
        };
    }
}
