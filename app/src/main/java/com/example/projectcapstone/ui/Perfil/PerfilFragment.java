package com.example.projectcapstone.ui.Perfil;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.TipoReporte;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PerfilFragment extends Fragment {
    private TextView tvCantidadPublicaciones, tvCantidadSeguidores, tvCantidadSeguidos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        tvCantidadPublicaciones = rootView.findViewById(R.id.tvCantidadPublicaciones);
        tvCantidadSeguidores = rootView.findViewById(R.id.tvCantidadSeguidores);
        tvCantidadSeguidos = rootView.findViewById(R.id.tvCantidadSeguidos);

        cargarCantidadPublicaciones(tvCantidadPublicaciones);

        return rootView;
    }

    public void cargarCantidadPublicaciones(TextView tvCantidadPublicaciones) {
        int idEstudiante = 1;
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_cantidad_publicaciones.php?idEstudiante=" + idEstudiante;

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