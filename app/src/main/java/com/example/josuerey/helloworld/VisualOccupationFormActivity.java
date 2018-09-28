package com.example.josuerey.helloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VisualOccupationFormActivity extends AppCompatActivity {

    private EditText editTextStudyVia;
    private EditText editTextWayDirection;
    private EditText editTextWaterConditions;
    private EditText editTextCross;
    private EditText editTextEnc;
    private EditText editTextObservations;
    private Button btnStartStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.visual_occupation_activity);

        editTextStudyVia = (EditText) findViewById(R.id.editTextStudyVia);
        editTextWayDirection = (EditText) findViewById(R.id.editTextWayDirection);
        editTextWaterConditions = (EditText) findViewById(R.id.editTextWaterConditions);
        editTextCross = (EditText) findViewById(R.id.editTextCross);
        editTextEnc = (EditText) findViewById(R.id.editTextEnc);
        editTextObservations = (EditText) findViewById(R.id.editTextObservations);
        btnStartStudy = (Button) findViewById(R.id.btnStartStudy);

        btnStartStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsValidateSuccess()) {
                    Toast.makeText(VisualOccupationFormActivity.this, "Lograste entrar",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean fieldsValidateSuccess() {

        if(TextUtils.isEmpty(editTextStudyVia.getText().toString())) {
            editTextStudyVia.setError("Favor de ingresar una via de estudio");
            editTextStudyVia.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(editTextWayDirection.getText().toString())) {
            editTextWayDirection.setError("Favor de ingresar un carril / sentido");
            editTextWayDirection.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(editTextCross.getText().toString())) {
            editTextCross.setError("Favor de ingresar un cruce");
            editTextCross.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(editTextEnc.getText().toString())) {
            editTextEnc.setError("Favor de ingresar un nombre");
            editTextEnc.requestFocus();
            return false;
        }

        return true;
    }
}
