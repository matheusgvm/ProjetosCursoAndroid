package com.vilaca.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.vilaca.instagram.R;
import com.vilaca.instagram.activity.ComentariosActivity;
import com.vilaca.instagram.helper.ConfiguracaoFirebase;
import com.vilaca.instagram.helper.UsuarioFirebase;
import com.vilaca.instagram.model.Feed;
import com.vilaca.instagram.model.PostagemCurtida;
import com.vilaca.instagram.model.Usuario;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterFeed.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterFeed.MyViewHolder holder, int position) {
        final Feed feed = listaFeed.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        if (!uriFotoUsuario.toString().isEmpty() ){
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        }

        if(!uriFotoPostagem.toString().isEmpty()){
            Glide.with(context).load(uriFotoPostagem).into(holder.fotoPostagem);
        }


        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());

        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId());
                context.startActivity(i);
            }
        });

        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagens-curtidas")
                .child(feed.getId());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int qtdCurtidas = 0;
                if(dataSnapshot.hasChild("qtdCurtidas")){
                    PostagemCurtida postagemCurtida = dataSnapshot.getValue(PostagemCurtida.class);
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();
                }

                if(dataSnapshot.hasChild(usuarioLogado.getId())){
                    holder.likeButton.setLiked(true);
                }else holder.likeButton.setLiked(false);

                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtdCurtidas(qtdCurtidas);

                holder.likeButton.setOnLikeListener(
                        new OnLikeListener() {
                            @Override
                            public void liked(LikeButton likeButton) {
                                curtida.salvar();
                                holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                            }

                            @Override
                            public void unLiked(LikeButton likeButton) {
                                curtida.remover();
                                holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                            }
                        }
                );
                holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoPerfil = itemView.findViewById(R.id.imagePerfilPostagem);
            fotoPostagem = itemView.findViewById(R.id.imagePostagemSelecionada);
            nome = itemView.findViewById(R.id.textPerfilPostagem);
            qtdCurtidas = itemView.findViewById(R.id.textQtdeCurtidasPostagem);
            descricao = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);

        }
    }

}
