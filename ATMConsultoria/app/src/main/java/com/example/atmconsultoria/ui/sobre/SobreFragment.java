package com.example.atmconsultoria.ui.sobre;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atmconsultoria.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 */
public class SobreFragment extends Fragment {


    public SobreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Element versao =new Element();
        versao.setTitle("Versão 1.0");

        return new AboutPage(getActivity())
                .setImage(R.drawable.logo)
                .addGroup("Entre em contato")
                .addEmail("atendimento@atmconsultoria.com.br", "Envie um e-mail")
                .addWebsite("https://www.google.com.br/", "Acesse nosso site")
                .addGroup("Redes sociais")
                .addFacebook("google","Facebook")
                .addInstagram("google","Instagram")
                .addTwitter("google","Twitter")
                .addYoutube("google","YouTube")
                .addGitHub("google","GitHub")
                .addPlayStore("com.google.android.googlequicksearchbox", "Baixe o App")
                .addItem(versao)
                .create();
    }
}
