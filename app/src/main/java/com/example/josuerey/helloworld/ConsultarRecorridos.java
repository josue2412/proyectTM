package com.example.josuerey.helloworld;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.josuerey.helloworld.utilidades.Utilidades;

public class ConsultarRecorridos extends AppCompatActivity {

    EditText campoId, campoNom_Ruta, campoVia, campoNum_Econ, campoEncuestador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_recorridos);


        campoId=(EditText) findViewById(R.id.documentoId);
        campoNom_Ruta=(EditText) findViewById(R.id.campoNom_Ruta);
        campoVia=(EditText) findViewById(R.id.campoVia);
        campoNum_Econ=(EditText) findViewById(R.id.campoNum_Econ);
        campoEncuestador=(EditText) findViewById(R.id.campoEncuestador);
    }

    public void onClick(View view){

        switch (view.getId()){

            case R.id.btnConsultar:
                //consultar();
                consultarSQL();
            break;
            case  R.id.btnActualizar:
            break;
            case R.id.btnEliminar:
            break;

        }
    }

    private void consultarSQL() {

        String[] parametros={campoId.getText().toString()};

        try {

            /*campoNom_Ruta.setText(cursor.getString(0));
            campoVia.setText(cursor.getString(1));
            campoNum_Econ.setText(cursor.getString(2));
            campoEncuestador.setText(cursor.getString(3));*/

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"El documento no existe",Toast.LENGTH_LONG).show();
            limpiar();
        }
    }

    private void consultar() {

        String[] parametros={campoId.getText().toString()};
        String[] campos={Utilidades.CAMPO_NOM_RUTA,Utilidades.CAMPO_VIA,Utilidades.CAMPO_NUM_ECON,Utilidades.CAMPO_ENCUESTADOR};

        try {

            /*campoNom_Ruta.setText(cursor.getString(0));
            campoVia.setText(cursor.getString(1));
            campoNum_Econ.setText(cursor.getString(2));
            campoEncuestador.setText(cursor.getString(3));
            cursor.close();*/
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"El documento no existe",Toast.LENGTH_LONG).show();
            limpiar();
        }


    }

    private void limpiar() {
        campoNom_Ruta.setText("");
        campoVia.setText("");
        campoNum_Econ.setText("");
        campoEncuestador.setText("");
    }
}
