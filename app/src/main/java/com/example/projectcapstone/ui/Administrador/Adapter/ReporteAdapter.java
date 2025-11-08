package com.example.projectcapstone.ui.Administrador.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder>{
    private final List<ReporteModel> listaReportes;
    private final OnReporteClickListener listener;

    public interface OnReporteClickListener {
        void onReporteClick(ReporteModel reporte);
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

        holder.tvUsuario.setText("👤 " + reporte.getUsuarioReporta());
        holder.tvMotivo.setText("📌 Motivo: " + reporte.getMotivo());
        holder.tvTitulo.setText("🗒️ Publicación: " + reporte.getTitulo());
        holder.tvContenido.setText("📝 " + reporte.getContenido());
        holder.tvFecha.setText("📅 " + reporte.getFecha());

        String estadoTexto;
        switch (reporte.getEstado()) {
            case "1": estadoTexto = "Pendiente"; break;
            case "2": estadoTexto = "Eliminado"; break;
            case "3": estadoTexto = "Archivado"; break;
            case "4": estadoTexto = "Advertido"; break;
            default: estadoTexto = "Desconocido"; break;
        }
        holder.tvEstado.setText("⚙️ Estado: " + estadoTexto);

        holder.itemView.setOnClickListener(v -> listener.onReporteClick(reporte));
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
