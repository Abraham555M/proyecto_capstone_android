package com.example.projectcapstone.ui.Inicio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PerfilEmprendedorFragment extends Fragment {
    private TextView tvCantidadPublicaciones, tvCantidadSeguidores, tvCantidadSeguidos, tvNombre, tvSede, tvTelefono;
    private int idEstudianteEmprendedor = -1; // ID del estudiante emprendedor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_emprendedor, container, false);

        tvCantidadPublicaciones = rootView.findViewById(R.id.tvCantidadPublicaciones);
        tvCantidadSeguidores = rootView.findViewById(R.id.tvCantidadSeguidores);
        tvCantidadSeguidos = rootView.findViewById(R.id.tvCantidadSeguidos);

        tvNombre = rootView.findViewById(R.id.tvNombre);
        tvSede = rootView.findViewById(R.id.tvSede);
        tvTelefono = rootView.findViewById(R.id.tvTelefono);


        Bundle args = getArguments();
        if (args != null) {
            int idEmprendimiento = args.getInt("idEmprendimiento", -1);

            if (idEmprendimiento != -1) {
                obtenerDatosEmprendedor(idEmprendimiento);
            }
        }

        return rootView;
    }

    private void obtenerDatosEmprendedor(int idEmprendimiento) {
        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_obtener_estudiante.php?idEmprendimiento=" + idEmprendimiento;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(respuesta);

                    String nombreCompleto = json.getString("nombre_completo");
                    String sede = json.getString("nom_sede");

                    idEstudianteEmprendedor = json.getInt("id_estudiante");

                    tvNombre.setText(nombreCompleto);
                    tvSede.setText(sede);

                    cargarCantidadPublicaciones(idEstudianteEmprendedor);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCantidadPublicaciones(int idEstudianteEmprendedor) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_cantidad_publicaciones.php?idEstudiante=" + idEstudianteEmprendedor;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONObject jsonObject = new JSONObject(respuesta);

                    int total = jsonObject.getInt("total_publicaciones");
                    tvCantidadPublicaciones.setText(String.valueOf(total));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


}