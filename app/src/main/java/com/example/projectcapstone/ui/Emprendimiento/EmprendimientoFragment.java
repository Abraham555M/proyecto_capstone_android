package com.example.projectcapstone.ui.Emprendimiento;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
//import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Emprendimiento.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Clases.Categoria;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.entity.mime.Header;

public class EmprendimientoFragment extends Fragment implements View.OnClickListener{
    private Button btnMisEmprendimientos;

    RecyclerView recyclerCategorias;
    CategoriaAdapter adapter;
    List<Categoria> listaCategorias = new ArrayList<>();
    private SessionManager session;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emprendimiento, container, false);

        session = new SessionManager(requireContext());
        recyclerCategorias = rootView.findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CategoriaAdapter(listaCategorias, new CategoriaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria categoria) {
                // Aquí navegas a tu fragmento de crear emprendimiento
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

                // Si quieres pasar datos (ej. id de la categoría)
                Bundle bundle = new Bundle();
                bundle.putInt("id_categoria", categoria.getIdCategoria());
                bundle.putString("nombre_categoria", categoria.getNomCategoria());

                navController.navigate(R.id.action_nav_emprendimiento_to_nuevoEmprendimientoFragment, bundle);
            }
        });

        recyclerCategorias.setAdapter(adapter);

        btnMisEmprendimientos = rootView.findViewById(R.id.btnMisEmprendimientos);
        btnMisEmprendimientos.setOnClickListener(this);

        // TODO: Llamar a tu API o BD para llenar categorías
        cargarCategoriasDesdeBD();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnMisEmprendimientos){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_emprendimiento_to_nav_emprendimiento_lista);
        }
    }

    private void cargarCategoriasDesdeBD() {
        String url = ServidorConfig.URL_SERVIDOR + "categoria/obtener_categorias.php";

        // Obtener el ID del estudiante con la funciona seesionManager
        int idEstudiante = session.getIdEstudiante();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_estudiante", idEstudiante);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                try {
                    listaCategorias.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);

                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        String imagen = obj.getString("imagen");

                        Categoria categoria = new Categoria(id, nombre, imagen);
                        listaCategorias.add(categoria);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }
}