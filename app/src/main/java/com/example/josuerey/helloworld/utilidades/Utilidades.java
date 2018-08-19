package com.example.josuerey.helloworld.utilidades;

public class Utilidades {

    //Constantes
    public static String  TABLA_RECORRIDO= "recorrido";
    public static String  CAMPO_ID= "id";
    public static String  CAMPO_NOM_RUTA= "nom_ruta";
    public static String  CAMPO_VIA= "via";
    public static String  CAMPO_NUM_ECON= "num_econ";
    public static String  CAMPO_ENCUESTADOR= "encuestador";

    public static final String CREAR_TABLA_RECORRIDO = "CREATE TABLE "+ TABLA_RECORRIDO +" ("+ CAMPO_ID +" INTEGER, "+ CAMPO_NOM_RUTA +" TEXT, "+ CAMPO_VIA +" TEXT, "+ CAMPO_NUM_ECON +" TEXT, "+ CAMPO_ENCUESTADOR +" TEXT)";


    public static final String TABLA_DRECORRIDOS="drecorridos";
    public static final String CAMPO_ID_DREC="id_drec";
    public static final String CAMPO_ID_REC="id_rec";
    public static final String CAMPO_DHORA_REF="dhora_ref";
    public static final String CAMPO_DCOORD="dcoord";

    public static final String CREAR_TABLA_DRECORRIDO = "CREATE TABLE "+
            TABLA_DRECORRIDOS + " ("+ CAMPO_ID_DREC +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CAMPO_ID_REC +
            " INTEGER, "+ CAMPO_DHORA_REF +" INTEGER,"+
            CAMPO_DCOORD +" INTEGER)";


    public static final String TABLA_PARADAS="paradas";
    public static final String CAMPO_ID_PARADAS="id_paradas";
    //public static final String CAMPO_ID_REC="id_rec";
    public static final String CAMPO_HORA_REF="hora_ref";
    public static final String CAMPO_T_PARADA="t_parada";
    public static final String CAMPO_T_PARADA2="t_parada2";
    public static final String CAMPO_SUBEN="suben";
    public static final String CAMPO_BAJAN="bajan";
    public static final String CAMPO_P_ABORDO="p_abordo";
    public static final String CAMPO_COORD="coord";

    public static final String CREAR_TABLA_PARADAS = "CREATE TABLE "+
            TABLA_PARADAS + " ("+ CAMPO_ID_PARADAS +" INTEGER, "+ CAMPO_ID_REC +
            " INTEGER, "+ CAMPO_HORA_REF +" INTEGER, "+ CAMPO_T_PARADA +" INTEGER, "+
            CAMPO_T_PARADA2 +" INTEGER, "+ CAMPO_SUBEN +" INTEGER,"+
            CAMPO_BAJAN +" INTEGER,"+ CAMPO_P_ABORDO +" INTEGER,"+
            CAMPO_COORD +" INTEGER)";

}
