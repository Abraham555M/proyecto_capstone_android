package com.example.projectcapstone.ui.Perfil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.ColaboracionPerfil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ColaboradoresPerfilAdapter extends RecyclerView.Adapter<ColaboradoresPerfilAdapter.ViewHolder> {

    private final Context context;
    private final List<ColaboracionPerfil> colaboraciones;

    public ColaboradoresPerfilAdapter(Context context, List<ColaboracionPerfil> colaboraciones) {
        this.context = context;
        this.colaboraciones = colaboraciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_colaborador_perfil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ColaboracionPerfil col = colaboraciones.get(position);

        // Nombre completo
        String nombreCompleto = col.getNomEstudiante() + " " +
                col.getApePatEstudiante() + " " +
                col.getApeMatEstudiante();
        holder.tvNombreColaborador.setText(nombreCompleto);

        // Email
        holder.tvEmailColaborador.setText(col.getEmaEstudiante());

        // Fecha formateada
        try {
            String fecha = col.getFchColaboracion();
            // Formato de entrada: "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat salida = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String fechaFormateada = salida.format(entrada.parse(fecha));
            holder.tvFechaColaboracion.setText("Desde: " + fechaFormateada);
        } catch (Exception e) {
            holder.tvFechaColaboracion.setText("Desde: -");
        }

        // Imagen del colaborador (usa Glide)
        Glide.with(context)
                .load("https://ui-avatars.com/api/?name=" + nombreCompleto.replace(" ", "+"))
                .placeholder(R.drawable.ic_user_avatar)
                .into(holder.ivAvatarColaborador);
    }

    @Override
    public int getItemCount() {
        return colaboraciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatarColaborador;
        TextView tvNombreColaborador, tvEmailColaborador, tvFechaColaboracion, tvEstadoColaboracion;
        ImageView ivEstadoColaboracion;
        com.google.android.material.card.MaterialCardView cardEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatarColaborador = itemView.findViewById(R.id.ivAvatarColaborador);
            tvNombreColaborador = itemView.findViewById(R.id.tvNombreColaborador);
            tvEmailColaborador = itemView.findViewById(R.id.tvEmailColaborador);
            tvFechaColaboracion = itemView.findViewById(R.id.tvFechaColaboracion);
            tvEstadoColaboracion = itemView.findViewById(R.id.tvEstadoColaboracion);
            ivEstadoColaboracion = itemView.findViewById(R.id.ivEstadoColaboracion);
            cardEstado = itemView.findViewById(R.id.cardEstado);
        }
    }
}