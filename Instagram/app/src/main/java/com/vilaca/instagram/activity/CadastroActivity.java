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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.vilaca.instagram.R;
import com.vilaca.instagram.helper.ConfiguracaoFirebase;
import com.vilaca.instagram.helper.UsuarioFirebase;
import com.vilaca.instagram.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoUsuario, campoEmail, campoSenha;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
        progressBar.setVisibility(View.GONE);
    }

    public  void inicializarComponentes(){
        campoUsuario = findViewById(R.id.editCadastroUsuario);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        progressBar = findViewById(R.id.progressCadastro);
    }

    public void validarCadastroUsuario(View view){
        String textoNome = campoUsuario.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoNome.isEmpty()){
            if (!textoEmail.isEmpty()){
                if (!textoSenha.isEmpty()){

                    usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario();

                }else Toast.makeText(CadastroActivity.this,"Preencha a senha",Toast.LENGTH_SHORT).show();
            }else Toast.makeText(CadastroActivity.this,"Preencha o e-mail",Toast.LENGTH_SHORT).show();
        }else Toast.makeText(CadastroActivity.this,"Preencha o nome",Toast.LENGTH_SHORT).show();
    }

    public void cadastrarUsuario(){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    try{
                        progressBar.setVisibility(View.GONE);

                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setId(idUsuario);
                        usuario.salvar();
                        UsuarioFirebase.atualizaNomeUsuario(usuario.getNome());

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    progressBar.setVisibility(View.GONE);
                    String excecao = "";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esse e-mail já foi utilizado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

}
