package com.example.projectcapstone.ui.Emprendimiento;

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
import com.example.projectcapstone.ui.Clases.ItemOffsetDecoration;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Clases.Categoria;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emprendimiento, container, false);

        recyclerCategorias = rootView.findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Espaciado uniforme de 16dp
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        recyclerCategorias.addItemDecoration(new ItemOffsetDecoration(spacingInPixels));

        adapter = new CategoriaAdapter(getContext(), listaCategorias);
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
        String url = ServidorConfig.URL_SERVIDOR + "categoria/obtener_categorias.php"; // Ajusta tu endpoint

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                try {

                    Toast.makeText(getContext(), "Cargando categorías", Toast.LENGTH_SHORT).show();

                    listaCategorias.clear(); // limpiar para evitar duplicados
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);

                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        String imagen = obj.getString("imagen");

                        Categoria categoria = new Categoria(id, nombre, imagen);
                        listaCategorias.add(categoria);
                    }

                    // Notificar al adapter que hay datos nuevos
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }
}