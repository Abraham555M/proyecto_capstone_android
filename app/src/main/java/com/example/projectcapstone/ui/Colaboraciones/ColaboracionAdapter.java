package com.example.projectcapstone.ui.Colaboraciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;

import java.util.List;
import com.bumptech.glide.Glide;

public class ColaboracionAdapter extends RecyclerView.Adapter<ColaboracionAdapter.ViewHolder>{
    private Context context;
    private List<Colaboracion> listaColaboraciones;

    public ColaboracionAdapter(Context context, List<Colaboracion> listaColaboraciones) {
        this.context = context;
        this.listaColaboraciones = listaColaboraciones;
    }

    @NonNull
    @Override
    public ColaboracionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_colaboracion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColaboracionAdapter.ViewHolder holder, int position) {
        Colaboracion colaboracion = listaColaboraciones.get(position);

        // Mostrar mensaje
        holder.tvMensajeColaboracion.setText(colaboracion.getMen_colaboracion());

        // Mostrar imagen de la publicación
        if (colaboracion.getPublicacion() != null && colaboracion.getPublicacion().getImagenUrl()!= null) {
            Glide.with(context)
                    .load(colaboracion.getPublicacion().getImagenUrl())
                    .placeholder(R.drawable.ic_inicio) // imagen por defecto
                    .error(R.drawable.ic_placeholder)  // si falla
                    .into(holder.imgUsuario);
        } else {
            holder.imgUsuario.setImageResource(R.drawable.ic_inicio);
        }

        // Mostrar botones o texto según el estado
        if (colaboracion.getEst_colaboracion() == 0) {
            holder.layoutBotones.setVisibility(View.VISIBLE);
            holder.tvConfirmacion.setVisibility(View.GONE);
        } else {
            holder.layoutBotones.setVisibility(View.GONE);
            holder.tvConfirmacion.setVisibility(View.VISIBLE);
        }

        // Botón ACEPTAR
        holder.btnAceptar.setOnClickListener(v -> {
            colaboracion.setEst_colaboracion(1);
            notifyItemChanged(position);
            Toast.makeText(context, "¡Ahora son colaboradores!", Toast.LENGTH_SHORT).show();
        });

        // Botón RECHAZAR
        holder.btnRechazar.setOnClickListener(v -> {
            listaColaboraciones.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
        });

        // Opciones
        holder.btnOpciones.setOnClickListener(v ->
                Toast.makeText(context, "Opciones no disponibles aún", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaColaboraciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUsuario, btnOpciones;
        TextView tvMensajeColaboracion, tvConfirmacion;
        Button btnAceptar, btnRechazar;
        LinearLayout layoutBotones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            tvMensajeColaboracion = itemView.findViewById(R.id.tvMensajeColaboracion);
            layoutBotones = itemView.findViewById(R.id.layoutBotones);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            tvConfirmacion = itemView.findViewById(R.id.tvConfirmacion);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
