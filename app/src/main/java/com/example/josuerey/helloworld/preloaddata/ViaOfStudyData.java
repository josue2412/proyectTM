package com.example.josuerey.helloworld.preloaddata;

import android.support.annotation.NonNull;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;

public class ViaOfStudyData {
    @NonNull
    public static ViaOfStudy[] getBusRouteData() {
        return new ViaOfStudy[] {
                ViaOfStudy.builder().id(1).via("1").build(),
                ViaOfStudy.builder().id(2).via("2").build(),
                ViaOfStudy.builder().id(3).via("3").build(),
                ViaOfStudy.builder().id(4).via("4").build(),
                ViaOfStudy.builder().id(5).via("5").build(),
                ViaOfStudy.builder().id(6).via("6").build(),
                ViaOfStudy.builder().id(7).via("7").build(),
                ViaOfStudy.builder().id(8).via("8").build(),
                ViaOfStudy.builder().id(9).via("9").build(),
                ViaOfStudy.builder().id(10).via("10").build()
        };
    }
}
