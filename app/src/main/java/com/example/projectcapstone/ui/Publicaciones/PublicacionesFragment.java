package com.example.projectcapstone.ui.Publicaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PublicacionesFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private PublicacionAdapter adapter;
    private List<Publicacion> publicaciones;
    private Button btnAgregarPublicacion;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publicaciones, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAgregarPublicacion = rootView.findViewById(R.id.btnAgregarPublicacion);

        publicaciones = new ArrayList<>();
        adapter = new PublicacionAdapter(publicaciones, getContext());
        recyclerView.setAdapter(adapter);
        btnAgregarPublicacion.setOnClickListener(this);


        cargarPublicaciones();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnAgregarPublicacion){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_publicaciones_to_nuevaPublicacionFragment);
        }
    }

    private void cargarPublicaciones() {
        String URL = ServidorConfig.URL_SERVIDOR + "publicacion/listar_publicaciones.php";

        AsyncHttpClient client = new AsyncHttpClient();

        SharedPreferences prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        int idEstudiante = prefs.getInt("id_estudiante", -1);

        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("servidorConfig", ServidorConfig.URL_FOTOS_SERVIDOR);

        client.get(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray array = new JSONArray(response);

                    publicaciones.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        String imagen = obj.getString("imagen_url");

                        // 👇 Si no es URL absoluta (http/https), concatenamos
                        if (!imagen.startsWith("http")) {
                            imagen = ServidorConfig.URL_FOTOS_SERVIDOR + imagen;
                        }

                        publicaciones.add(new Publicacion(
                                obj.getInt("id"),
                                obj.getString("titulo"),
                                obj.getString("descripcion"),
                                imagen
                        ));
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error procesando publicaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error cargando publicaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }
}