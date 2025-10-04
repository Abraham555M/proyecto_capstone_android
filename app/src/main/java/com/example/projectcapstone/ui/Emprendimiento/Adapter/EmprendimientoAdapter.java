package com.example.projectcapstone.ui.Emprendimiento.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Emprendimiento;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmprendimientoAdapter extends RecyclerView.Adapter<EmprendimientoAdapter.ViewHolder> {

    private Context context;
    private List<Emprendimiento> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(Emprendimiento empr);
        void onEliminarClick(Emprendimiento empr);
    }

    public EmprendimientoAdapter(Context context, List<Emprendimiento> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emprendimiento_grid, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emprendimiento empr = lista.get(position);

        // Nombre
        holder.tvNombre.setText(empr.getNom_emprendimiento());

        Picasso.get()
                .load(empr.getImg_per_emprendimiento())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.imgEm);

        // Botones solo asignan acción, el texto se mantiene igual que en XML
        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(empr));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(empr));
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEm;
        TextView tvNombre;
        Button btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEm = itemView.findViewById(R.id.ivEmprendimientoIcon);
            tvNombre = itemView.findViewById(R.id.tvEmprendimientoName);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar1);
        }
    }
}
