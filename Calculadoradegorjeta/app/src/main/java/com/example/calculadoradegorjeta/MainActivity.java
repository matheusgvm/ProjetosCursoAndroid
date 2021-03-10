package com.example.calculadoradegorjeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private EditText editValor;
    private TextView textPorcentagem;
    private TextView textGorjeta;
    private TextView textTotal;
    private SeekBar seekBarGorjeta;

    private double porcentagem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editValor =  findViewById(R.id.editValor);
        textPorcentagem =  findViewById(R.id.textPorcentagem);
        textGorjeta =  findViewById(R.id.textGorjeta);
        textTotal =  findViewById(R.id.textTotal);
        seekBarGorjeta =  findViewById(R.id.seekBarGorjeta);

        seekBarGorjeta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                porcentagem = progress;
                textPorcentagem.setText(Math.round(porcentagem)+ "%");
                calacular();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void calacular(){
        String valorRecuperado = editValor.getText().toString();
        if (valorRecuperado == null || valorRecuperado.equals("")){
            Toast.makeText(getApplicationContext(), "Digite um valor primeiro!", Toast.LENGTH_LONG).show();
        }else {
            double valorDigitado = Double.parseDouble(valorRecuperado);

            double gorjeta = valorDigitado * (porcentagem/100);

            BigDecimal gorjetaFormatada = new BigDecimal(gorjeta).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal totalFormatado = new BigDecimal(valorDigitado + gorjetaFormatada.doubleValue()).setScale(2, RoundingMode.HALF_EVEN);

            textGorjeta.setText("R$ " + gorjetaFormatada.doubleValue());
            textTotal.setText("R$ " + (totalFormatado.doubleValue()));
        }
    }
}
