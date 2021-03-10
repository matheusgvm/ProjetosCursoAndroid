package com.vilaca.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vilaca.instagram.R;
import com.vilaca.instagram.helper.ConfiguracaoFirebase;
import com.vilaca.instagram.helper.Permissao;
import com.vilaca.instagram.helper.UsuarioFirebase;
import com.vilaca.instagram.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    public static final int SELECAO_GALERIA = 200;

    private CircleImageView imagemEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;

    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getfirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentifacadorUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        inicializarComponentes();

        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imagemEditarPerfil);
        }else imagemEditarPerfil.setImageResource(R.drawable.avatar);

        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                UsuarioFirebase.atualizaNomeUsuario(nomeAtualizado);
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar();
                Toast.makeText(EditarPerfilActivity.this,"Dados alterados com sucesso!",Toast.LENGTH_SHORT).show();
            }
        });


        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });
    }

    public void inicializarComponentes(){
        imagemEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                    Uri localImagemSelecionda = data.getData();

                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        imagem = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), localImagemSelecionda));
                    } else
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionda);

                    break;
                }

                if(imagem!= null){
                    imagemEditarPerfil.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,"Erro ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditarPerfilActivity.this,"Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();

                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizaFotoUsuario(url);
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private  void atualizaFotoUsuario(Uri url){
        UsuarioFirebase.atualizaFotoUsuario(url);

        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
