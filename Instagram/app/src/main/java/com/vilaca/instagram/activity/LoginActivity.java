package com.vilaca.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.vilaca.instagram.R;
import com.vilaca.instagram.helper.ConfiguracaoFirebase;
import com.vilaca.instagram.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth auteticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auteticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        inicializarComponentes();
    }

    public void inicializarComponentes(){
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        progressBar = findViewById(R.id.progressLogin);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = auteticacao.getCurrentUser();
        if (usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }

    public void logarUsuario(View view){

        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if(!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);
                validarLogin();

            }else Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();

    }

    public void validarLogin(){
        progressBar.setVisibility(View.VISIBLE);
        auteticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    abrirTelaPrincipal();
                    finish();
                }else {

                    String excecao = "";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem a um usúario cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário\n" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    public void  abrirTelaPrincipal(){
        startActivity(new Intent(this, MainActivity.class));
    }



    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

}
