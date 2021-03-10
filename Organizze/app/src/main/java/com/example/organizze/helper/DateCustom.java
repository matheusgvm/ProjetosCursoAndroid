package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");
        String dataString = simpleDateFormat.format(data);
        return dataString;

    }

    public static String mesAnoDataEscolhida(String data){

        String retornoData[] = data.split("/");

        return retornoData[1] + retornoData[2];

    }

}
