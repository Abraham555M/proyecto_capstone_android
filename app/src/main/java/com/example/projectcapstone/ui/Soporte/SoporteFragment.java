package com.example.projectcapstone.ui.Soporte;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SoporteFragment extends Fragment {

    private LinearLayout layoutPregunta1, layoutPregunta2, layoutPregunta3;
    private TextView tvRespuesta1, tvRespuesta2, tvRespuesta3;
    private ImageView iconExpand1, iconExpand2, iconExpand3;
    private EditText etSolicitud;
    private Button btnEnviar;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_soporte, container, false);

        // Referencias a los elementos del layout
        layoutPregunta1 = rootView.findViewById(R.id.layoutPregunta1);
        layoutPregunta2 = rootView.findViewById(R.id.layoutPregunta2);
        layoutPregunta3 = rootView.findViewById(R.id.layoutPregunta3);

        tvRespuesta1 = rootView.findViewById(R.id.tvRespuesta1);
        tvRespuesta2 = rootView.findViewById(R.id.tvRespuesta2);
        tvRespuesta3 = rootView.findViewById(R.id.tvRespuesta3);

        iconExpand1 = rootView.findViewById(R.id.iconExpand1);
        iconExpand2 = rootView.findViewById(R.id.iconExpand2);
        iconExpand3 = rootView.findViewById(R.id.iconExpand3);

        etSolicitud = rootView.findViewById(R.id.etSolicitud);
        btnEnviar = rootView.findViewById(R.id.btnEnviar);

        // Configurar listeners para expandir/colapsar preguntas
        configurarAcordeon(layoutPregunta1, tvRespuesta1, iconExpand1);
        configurarAcordeon(layoutPregunta2, tvRespuesta2, iconExpand2);
        configurarAcordeon(layoutPregunta3, tvRespuesta3, iconExpand3);

        session = new SessionManager(requireContext());

        // Acción del botón Enviar
        btnEnviar.setOnClickListener(v -> enviarSolicitud());

        return rootView;
    }

    private void configurarAcordeon(LinearLayout layoutPregunta, TextView respuesta, ImageView icono) {
        layoutPregunta.setOnClickListener(v -> {
            if (respuesta.getVisibility() == View.GONE) {
                respuesta.setVisibility(View.VISIBLE);
                icono.setRotation(180f); // Gira el ícono hacia arriba
            } else {
                respuesta.setVisibility(View.GONE);
                icono.setRotation(0f); // Gira el ícono hacia abajo
            }
        });
    }

    private void enviarSolicitud() {
        String men_soporte = etSolicitud.getText().toString().trim();

        // ⚠️ Aquí deberías obtener el ID del estudiante logueado desde tu sesión o SharedPreferences
        int idEstudiante = session.getIdEstudiante(); // ejemplo temporal

        if (men_soporte.isEmpty()) {
            Toast.makeText(requireContext(), "Escribe tu solicitud antes de enviar.", Toast.LENGTH_SHORT).show();
            return;
        }
        String URL = ServidorConfig.URL_SERVIDOR + "soporte/envio_mensaje_soporte.php";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_estudiante", idEstudiante);
        params.put("men_soporte", men_soporte);

        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");
                    String msg = json.getString("msg");

                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    if(status.equals("success")) {
                        etSolicitud.setText(""); // Limpiar campo si fue exitoso

                        mostrarDialogoExito("Tu solicitud ha sido realizado correctamente y está pendiente de revisión.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void mostrarDialogoExito(String mensaje) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View vistaDialogo = inflater.inflate(R.layout.alert_dialog_res_positiva, null);

        // Referencias a los elementos de la vista
        TextView tvTitulo = vistaDialogo.findViewById(R.id.tvTituloExito);
        TextView tvMensaje = vistaDialogo.findViewById(R.id.tvMensajeExito);
        MaterialButton btnOk = vistaDialogo.findViewById(R.id.btnFuncionalidadExito);

        // Personalizar contenido dinámico
        tvTitulo.setText("Solicitud enviada");
        tvMensaje.setText(mensaje);

        // Crear el AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(vistaDialogo)
                .setCancelable(false)
                .create();

        // Botón de acción
        btnOk.setOnClickListener(v -> dialog.dismiss());

        // Mostrar con fondo transparente para respetar bordes redondeados
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }
}