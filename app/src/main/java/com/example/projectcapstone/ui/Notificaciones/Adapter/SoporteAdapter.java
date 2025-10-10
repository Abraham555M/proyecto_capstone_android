package com.example.projectcapstone.ui.Notificaciones.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Soporte;

import java.util.List;

public class SoporteAdapter extends RecyclerView.Adapter<SoporteAdapter.ViewHolder> {

    private Context context;
    private List<Soporte> listaSoportes;

    public SoporteAdapter(Context context, List<Soporte> listaSoportes) {
        this.context = context;
        this.listaSoportes = listaSoportes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud_ampliacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Soporte soporte = listaSoportes.get(position);

        holder.tvTituloSolicitud.setText("Solicitud #" + soporte.getId_soporte());
        holder.tvFechaSolicitud.setText(soporte.getFec_soporte());
        holder.tvEstadoSolicitud.setText(soporte.getEst_soporte());
        holder.tvMensajeSolicitud.setText(soporte.getMen_soporte());

        // Mostrar mensaje
        holder.tvMensajeSolicitud.setVisibility(View.VISIBLE);

        // Cambiar color según estado
        if ("Pendiente".equalsIgnoreCase(soporte.getEst_soporte())) {
            holder.tvEstadoSolicitud.setBackgroundResource(R.drawable.bg_estado_pendiente);
        } else {
            holder.tvEstadoSolicitud.setBackgroundResource(R.drawable.bg_estado_pendiente); // Cambiar despues
        }
    }

    @Override
    public int getItemCount() {
        return listaSoportes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloSolicitud, tvFechaSolicitud, tvEstadoSolicitud, tvMensajeSolicitud;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloSolicitud = itemView.findViewById(R.id.tvTituloSolicitud);
            tvFechaSolicitud = itemView.findViewById(R.id.tvFechaSolicitud);
            tvEstadoSolicitud = itemView.findViewById(R.id.tvEstadoSolicitud);
            tvMensajeSolicitud = itemView.findViewById(R.id.tvMensajeSolicitud);
        }
    }
}