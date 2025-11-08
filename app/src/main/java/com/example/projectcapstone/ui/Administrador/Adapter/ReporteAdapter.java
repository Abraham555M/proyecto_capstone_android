package com.example.projectcapstone.ui.Administrador.Adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder> {
    private final List<ReporteModel> listaReportes;
    private final OnReporteClickListener listener;

    public interface OnReporteClickListener {
        void onReporteClick(View view, ReporteModel reporte);
    }

    public ReporteAdapter(List<ReporteModel> listaReportes, OnReporteClickListener listener) {
        this.listaReportes = listaReportes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReporteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteAdapter.ViewHolder holder, int position) {
        ReporteModel reporte = listaReportes.get(position);

        holder.tvUsuario.setText(reporte.getUsuarioReporta());
        holder.tvMotivo.setText("Motivo: " + reporte.getMotivo());
        holder.tvTitulo.setText("Publicación: " + reporte.getTitulo());
        holder.tvContenido.setText(reporte.getContenido());
        holder.tvFecha.setText(reporte.getFecha());

        // Determinar texto y color del estado
        String estadoTexto;
        int colorEstado;

        switch (reporte.getEstado()) {
            case "1":
                estadoTexto = "Pendiente";
                colorEstado = 0xFFFFC107; // amarillo
                break;
            case "2":
                estadoTexto = "Eliminado";
                colorEstado = 0xFFE53935; // rojo
                break;
            case "3":
                estadoTexto = "Archivado";
                colorEstado = 0xFF757575; // gris
                break;
            case "4":
                estadoTexto = "Advertido";
                colorEstado = 0xFF1E88E5; // azul
                break;
            default:
                estadoTexto = "Desconocido";
                colorEstado = 0xFF9E9E9E; // gris claro
                break;
        }

        holder.tvEstado.setText(estadoTexto);

        // Crear fondo redondeado dinámico (sin usar drawable XML)
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(colorEstado);
        fondo.setCornerRadius(30f);
        holder.tvEstado.setBackground(fondo);
        holder.tvEstado.setTextColor(0xFFFFFFFF); // texto blanco

        // Click del item
        holder.itemView.setOnClickListener(v -> listener.onReporteClick(v, reporte));
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvMotivo, tvFecha, tvContenido, tvEstado, tvTitulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvMotivo = itemView.findViewById(R.id.tvMotivo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
