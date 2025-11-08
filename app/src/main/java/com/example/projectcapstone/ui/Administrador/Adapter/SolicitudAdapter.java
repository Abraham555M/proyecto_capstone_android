package com.example.projectcapstone.ui.Administrador.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder>{
    private List<Solicitud> lista;
    private Context context;
    private Runnable onEstadoCambiado; // ✅ callback

    public SolicitudAdapter(List<Solicitud> lista, Context context, Runnable onEstadoCambiado) {
        this.lista = lista;
        this.context = context;
        this.onEstadoCambiado = onEstadoCambiado;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);
        holder.tvEstudiante.setText("👤 " + solicitud.getNom_estudiante());
        holder.tvMensaje.setText(solicitud.getMen_soporte());
        holder.tvFecha.setText("📅 " + solicitud.getFec_soporte());
        holder.tvEstado.setText("Estado: " + solicitud.getEst_soporte());

        holder.btnAprobar.setOnClickListener(v -> actualizarEstado(solicitud.getId_soporte(), "Aceptado"));
        holder.btnRechazar.setOnClickListener(v -> actualizarEstado(solicitud.getId_soporte(), "Rechazado"));
    }

    private void actualizarEstado(int id, String estado) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_soporte", id);
        params.put("nuevo_estado", estado);

        client.post(ServidorConfig.URL_SERVIDOR + "administrador/actualizar_estado_solicitud.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, "Solicitud " + estado, Toast.LENGTH_SHORT).show();
                if (onEstadoCambiado != null) onEstadoCambiado.run();
            }
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEstudiante, tvMensaje, tvFecha, tvEstado;
        Button btnAprobar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEstudiante = itemView.findViewById(R.id.tvEstudiante);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
