package com.example.josuerey.helloworld.preloaddata;

import android.support.annotation.NonNull;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;

public class BusRouteData {

    @NonNull
    public static BusRoute[] getBusRouteData() {
        return new BusRoute[] {
                BusRoute.builder().id(1).routeName("187").build(),
                BusRoute.builder().id(2).routeName("054A").routeVia("EL CASTILLO").build(),
                BusRoute.builder().id(3).routeName("061").routeVia("-V1").build(),
                BusRoute.builder().id(4).routeName("171").routeVia("PINTITAS").build(),
                BusRoute.builder().id(5).routeName("644A").routeVia("K13 - PANTEON").build(),
                BusRoute.builder().id(6).routeName("644A").routeVia("K13-V1").build(),
                BusRoute.builder().id(7).routeName("645").routeVia("CHULAVISTA").build(),
                BusRoute.builder().id(8).routeName("A").routeVia("09").build(),
                BusRoute.builder().id(9).routeName("050A").routeVia("CHULAVISTA (ADOLF HORN)").build(),
                BusRoute.builder().id(10).routeName("623").routeVia("HUIZACHERA-FONTANA-ARBOLEDAS").build(),
                BusRoute.builder().id(11).routeName("623").routeVia("SANTA CRUZ DEL VALLE - HUIZACHERA - FONTANA").build(),
                BusRoute.builder().id(12).routeName("A").routeVia("08").build(),
                BusRoute.builder().id(13).routeName("A").routeVia("19").build(),
                BusRoute.builder().id(14).routeName("A").routeVia("20").build(),
                BusRoute.builder().id(15).routeName("180").build(),
                BusRoute.builder().id(16).routeName("176A").routeVia("CHULAVISTA").build(),
                BusRoute.builder().id(17).routeName("176A").routeVia("ROBLES").build(),
                BusRoute.builder().id(18).routeName("619").routeVia("(AMARILLA) CHULAVISTA - CENTRAL DE AUTOBUSES").build(),
                BusRoute.builder().id(19).routeName("619").routeVia("(ROJA) CIRCUITO 1").build(),
                BusRoute.builder().id(20).routeName("619").routeVia("(ROJA) CIRCUITO 2").build(),
                BusRoute.builder().id(21).routeName("619").routeVia("(VERDE) CHULAVISTA - ADOLF B. HORN").build(),
                BusRoute.builder().id(22).routeName("623A").routeVia("AQUA-CANTAROS").build(),
                BusRoute.builder().id(23).routeName("623A").routeVia("CANTAROS").build(),
                BusRoute.builder().id(24).routeName("623A").routeVia("CHULAVISTA-VALLE DORADO").build(),
                BusRoute.builder().id(25).routeName("623A").routeVia("PASEOS DEL VALLE-AQUA").build(),
                BusRoute.builder().id(26).routeName("A").routeVia("18").build(),
                BusRoute.builder().id(27).routeName("ARVENTO").routeVia("- TREN LIGERO").build(),
                BusRoute.builder().id(28).routeName("24").build(),
                BusRoute.builder().id(29).routeName("79").build(),
                BusRoute.builder().id(30).routeName("258").build(),
                BusRoute.builder().id(31).routeName("632").build(),
                BusRoute.builder().id(32).routeName("182").routeVia("LOMAS DEL SUR").build(),
                BusRoute.builder().id(33).routeName("182A").routeVia("BALCONES").build(),
                BusRoute.builder().id(34).routeName("182A").routeVia("OJO DE AGUA").build(),
                BusRoute.builder().id(35).routeName("183").routeVia("LOMAS DE SAN AGUSTIN").build(),
                BusRoute.builder().id(36).routeName("183A").routeVia("HACIENDAS DE SANTA FE - SANTA ANITA").build(),
                BusRoute.builder().id(37).routeName("186").routeVia("EUCALIPTOS - LA NORIA").build(),
                BusRoute.builder().id(38).routeName("186").routeVia("VALLE DE LOS EMPERADORES").build(),
                BusRoute.builder().id(39).routeName("258D").build(),
                BusRoute.builder().id(40).routeName("625").routeVia("COZUMEL").build(),
                BusRoute.builder().id(41).routeName("625A").routeVia("DIRECTO").build(),
                BusRoute.builder().id(42).routeName("175").routeVia("ROCA").build(),
                BusRoute.builder().id(43).routeName("175").routeVia("SANTA ANITA").build(),
                BusRoute.builder().id(44).routeName("175A").build(),
                BusRoute.builder().id(45).routeName("181").routeVia("(01)").build(),
                BusRoute.builder().id(46).routeName("181A").routeVia("(01A)").build(),
                BusRoute.builder().id(47).routeName("186").routeVia("(382) CALERA").build(),
                BusRoute.builder().id(48).routeName("INTEGRADOS").routeVia("SANTA LUCIA CIRCUITO").build(),
                BusRoute.builder().id(49).routeName("TLAJOMULCO").routeVia("-SANTA LUCIA").build(),
                BusRoute.builder().id(50).routeName("78").build(),
                BusRoute.builder().id(51).routeName("136").build(),
                BusRoute.builder().id(52).routeName("176").build(),
                BusRoute.builder().id(53).routeName("(180").routeVia(") Rodeo - Casas Bali").build(),
                BusRoute.builder().id(54).routeName("(180").routeVia(") Rodeo - Olivos").build(),
                BusRoute.builder().id(55).routeName("(180").routeVia(") Rodeo - Sabinos").build(),
                BusRoute.builder().id(56).routeName("080B").build(),
                BusRoute.builder().id(57).routeName("136A").build(),
                BusRoute.builder().id(58).routeName("171").routeVia("AQUA - CANTAROS - VERDE VALLE").build(),
                BusRoute.builder().id(59).routeName("176B").routeVia("JARDINES").build(),
                BusRoute.builder().id(60).routeName("176B").routeVia("VILLAS").build(),
                BusRoute.builder().id(61).routeName("176C").build(),
                BusRoute.builder().id(62).routeName("177").routeVia("EL VERDE").build(),
                BusRoute.builder().id(63).routeName("177A").routeVia("ALAMEDA").build(),
                BusRoute.builder().id(64).routeName("619").routeVia("(AZUL) LOMAS DE SAN AGUSTIN - CENTRAL DE AUTOB").build(),
                BusRoute.builder().id(65).routeName("644A").routeVia("LIEBRES").build(),
                BusRoute.builder().id(66).routeName("644A").routeVia("TAPATIO").build(),
                BusRoute.builder().id(67).routeName("ARVENTO").routeVia("- ANTIGUA CENTRAL CAMIONERA").build(),
                BusRoute.builder().id(68).routeName("AUTOBUSES").routeVia("CHAPALA").build(),
                BusRoute.builder().id(69).routeName("30").build(),
                BusRoute.builder().id(70).routeName("380").build(),
                BusRoute.builder().id(71).routeName("175").routeVia("LOMAS DE SAN AGUSTIN").build(),
                BusRoute.builder().id(72).routeName("186").routeVia("(382) LOPEZ MATEOS (LA NORIA)").build(),
                BusRoute.builder().id(73).routeName("380").routeVia("NUEVO MEXICO - LOS OLIVOS").build(),
                BusRoute.builder().id(74).routeName("619").routeVia("(NARANJA) CHULAVISTA - TREN LIGERO").build(),
                BusRoute.builder().id(75).routeName("619").routeVia("(VERDE) - 8 DE JULIIO - INGLATERRA").build(),
                BusRoute.builder().id(76).routeName("619A").routeVia("AGUA BLANCA (GAVILANES)").build(),
                BusRoute.builder().id(77).routeName("619A").routeVia("GUADALUPE GALLO").build(),
                BusRoute.builder().id(78).routeName("623A").routeVia("PASEOS DEL PRADO - LOS OLIVOS").build(),
                BusRoute.builder().id(79).routeName("(AUT").routeVia(". CHAPALA)ARVENTO - TREN LIGERO").build(),
                BusRoute.builder().id(80).routeName("INTEGRADOS").routeVia("SANTA LUCIA CIRCUITO*").build(),
                BusRoute.builder().id(81).routeName("644A").routeVia("JUNTAS-VERGEL").build(),
                BusRoute.builder().id(82).routeName("050A").routeVia("SANTA FE (8 DE JULIO)").build(),
                BusRoute.builder().id(83).routeName("050B").build(),
                BusRoute.builder().id(84).routeName("175E").routeVia("DOMUS (VILLAS DE SAN SEBASTIAN)").build()
        };
    }
}