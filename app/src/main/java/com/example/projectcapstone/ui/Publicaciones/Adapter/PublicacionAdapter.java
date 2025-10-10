package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder> {
    private List<Publicacion> lista;
    private Context context;
    private static final int MAX_LINES = 3; // El límite que definiste en el XML

    public PublicacionAdapter(List<Publicacion> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion pub = lista.get(position);
        holder.txtTitulo.setText(pub.getTitulo());

        // Cargar imagen con Glide
        Glide.with(context).load(pub.getImagenUrl()).into(holder.imgPublicacion);

        // 1. Configurar el estado inicial (o reciclado) de la descripción
        if (holder.isExpanded) {
            // Estado expandido
            holder.txtDescripcion.setMaxLines(Integer.MAX_VALUE);
            holder.txtDescripcion.setEllipsize(null);
            holder.txtVerMas.setText("Ver menos");
        } else {
            // Estado colapsado (por defecto)
            holder.txtDescripcion.setMaxLines(MAX_LINES);
            holder.txtDescripcion.setEllipsize(android.text.TextUtils.TruncateAt.END);
            holder.txtVerMas.setText("Ver más");
        }

        holder.txtDescripcion.setText(pub.getDescripcion());

        // 2. Verificar visibilidad de "Ver más"
        // Usamos post para asegurar que la vista ha sido medida (layout phase)
        // Esto solo es necesario al dibujar/reciclar la vista.
        holder.txtDescripcion.post(new Runnable() {
            @Override
            public void run() {
                // El botón "Ver más" debe ser visible si:
                // A) Está expandido (para que pueda ver "Ver menos")
                // B) El texto es largo y está truncado (para que pueda expandirlo)
                if (holder.isExpanded || isTextTruncated(holder.txtDescripcion)) {
                    holder.txtVerMas.setVisibility(View.VISIBLE);
                } else {
                    holder.txtVerMas.setVisibility(View.GONE);
                }
            }
        });

        // 3. Click en "Ver más / Ver menos"
        holder.txtVerMas.setOnClickListener(v -> {
            // Invertir el estado
            holder.isExpanded = !holder.isExpanded;

            if (holder.isExpanded) {
                // Expandir inmediatamente
                holder.txtDescripcion.setMaxLines(Integer.MAX_VALUE);
                holder.txtDescripcion.setEllipsize(null);
                holder.txtVerMas.setText("Ver menos");
            } else {
                // Colapsar inmediatamente
                holder.txtDescripcion.setMaxLines(MAX_LINES);
                holder.txtDescripcion.setEllipsize(android.text.TextUtils.TruncateAt.END);
                holder.txtVerMas.setText("Ver más");
            }

            // 💡 IMPORTANTE: Llamar a requestLayout para que el RecyclerView recalcule
            // el espacio de este item y el cambio de tamaño sea visible de inmediato.
            holder.itemView.requestLayout();

            // ❌ NO LLAMAR notifyItemChanged(holder.getLayoutPosition());
            // Llamar a notifyItemChanged fuerza un ciclo de reciclaje, lo que creaba el doble click.
        });

        holder.btnEditar.setOnClickListener(v -> {
            Toast.makeText(context, "Editar: " + pub.getTitulo(), Toast.LENGTH_SHORT).show();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            Toast.makeText(context, "Eliminar: " + pub.getTitulo(), Toast.LENGTH_SHORT).show();
        });
    }

    // Función auxiliar para determinar si el texto se truncaría
    private boolean isTextTruncated(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout == null) {
            return false;
        }

        // Si el número de líneas es mayor al máximo permitido
        if (layout.getLineCount() > MAX_LINES) {
            return true;
        }

        // Si la última línea visible contiene elipsis, significa que está truncado
        int lastLine = layout.getLineCount() - 1;
        if (lastLine >= 0) {
            return layout.getEllipsisCount(lastLine) > 0;
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPublicacion;
        TextView txtTitulo, txtDescripcion, txtVerMas;
        ImageButton btnEditar, btnEliminar;

        // Mantenemos el estado de expansión aquí
        boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPublicacion = itemView.findViewById(R.id.imgPublicacion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtVerMas = itemView.findViewById(R.id.txtVerMas);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}