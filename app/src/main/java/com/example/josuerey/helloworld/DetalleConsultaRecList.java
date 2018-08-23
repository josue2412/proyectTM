package com.example.josuerey.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.josuerey.helloworld.domain.Recorrido;

public class DetalleConsultaRecList extends AppCompatActivity {

    TextView campoId, campoNom_Ruta, campoVia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_consulta_rec_list);

        campoId = (TextView) findViewById(R.id.campoId);
        campoNom_Ruta = (TextView) findViewById(R.id.campoNom_Ruta);
        campoVia = (TextView) findViewById(R.id.campoVia);

        Bundle objetoEnviado=getIntent().getExtras();
        Recorrido rec=null;

        if(objetoEnviado!=null){
            rec= (Recorrido) objetoEnviado.getSerializable("recorrido");
            campoId.setText(rec.getId().toString());
            campoNom_Ruta.setText(rec.getNom_ruta().toString());
            campoVia.setText(rec.getVia().toString());

        }
    }
}
