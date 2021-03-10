package com.example.alcoolougasolina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editPrecoAlcool, editPrecoGasolina;
    private TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPrecoAlcool     = findViewById(R.id.editPrecoAlcool);
        editPrecoGasolina   = findViewById(R.id.editPrecoGasolina);
        resultado           = findViewById(R.id.textResultado);
    }

    public void calcularPreco(View view){
        String precoGasolina  = editPrecoGasolina.getText().toString();
        String precoAlcool    = editPrecoAlcool.getText().toString();

        Boolean camposValidados = validarCampos(precoAlcool, precoGasolina);
        if (camposValidados){

            Double valorAlcool = Double.parseDouble(precoAlcool);
            Double valorGasolina = Double.parseDouble(precoGasolina);

            if (valorAlcool / valorGasolina >= 0.7){
                resultado.setText("É melhor usar gasolina");
            }else resultado.setText("É melhor usar álcool");

        }else resultado.setText("Preencha todos os campos!");

    }

    public Boolean validarCampos(String pAlcool, String pGasolina){
        Boolean camposValidados = true;

        if (pAlcool == null || pAlcool.equals("")){
            camposValidados = false;
        }

        else if (pGasolina == null || pGasolina.equals("")){
            camposValidados = false;
        }

        return camposValidados;
    }
}
