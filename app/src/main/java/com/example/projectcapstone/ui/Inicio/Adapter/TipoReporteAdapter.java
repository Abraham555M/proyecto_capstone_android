package com.example.projectcapstone.ui.Inicio.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.TipoReporte;

import java.util.List;

public class TipoReporteAdapter extends RecyclerView.Adapter<TipoReporteAdapter.ViewHolder> {
    private List<TipoReporte> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TipoReporte tipo);
    }

    public TipoReporteAdapter(List<TipoReporte> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opciones_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TipoReporte tipo = lista.get(position);

        holder.tvReportTitle.setText(tipo.getNomTipoReporte());

        // Mostrar la flecha solo si quieres
        holder.ivArrow.setVisibility(View.GONE);

        // Evento click en toda la opción
        holder.itemView.setOnClickListener(v -> listener.onItemClick(tipo));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReportTitle;
        ImageView ivArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportTitle = itemView.findViewById(R.id.tvReportTitle);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}
