package com.example.josuerey.helloworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.Recorrido;
import com.example.josuerey.helloworld.utilidades.Utilidades;

import java.util.ArrayList;

public class ConsultarRecorridosLista extends AppCompatActivity {

    ListView listViewRecorridos;
    ArrayList<String> listaInformacion;
    ArrayList<Recorrido> listaRecorridos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_recorridos_lista);

        listViewRecorridos = (ListView) findViewById(R.id.listViewRecorridos);

        consultarListaRecorridos();

        ArrayAdapter adaptador=new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
        listViewRecorridos.setAdapter(adaptador);

        listViewRecorridos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String informacion="id: "+listaRecorridos.get(pos).getId()+"\n";
                informacion+="Ruta: "+listaRecorridos.get(pos).getNom_ruta()+"\n";
                informacion+="Via: "+listaRecorridos.get(pos).getVia()+"\n";

                Toast.makeText(getApplicationContext(),informacion,Toast.LENGTH_LONG).show();

                Recorrido rec=listaRecorridos.get(pos);

                Intent intent=new Intent(ConsultarRecorridosLista.this,DetalleConsultaRecList.class);

                Bundle bundle=new Bundle();
                bundle.putSerializable("recorrido",rec);

                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


    }

    private void consultarListaRecorridos() {

        Recorrido recorrido=null;
        listaRecorridos=new ArrayList<Recorrido>();


        /*while (cursor.moveToNext()){
            recorrido=new Recorrido();
            recorrido.setId(cursor.getInt(0));
            recorrido.setNom_ruta(cursor.getString(1));
            recorrido.setVia(cursor.getString(2));

            listaRecorridos.add(recorrido);
        }*/
        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion=new ArrayList<String>();

        for (int i=0; i<listaRecorridos.size();i++){
            listaInformacion.add(listaRecorridos.get(i).getId()+" - "
                    +listaRecorridos.get(i).getNom_ruta());
        }

    }
}