package com.example.projectcapstone.ui.Emprendimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Clases.Categoria;

import java.util.ArrayList;
import java.util.List;

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

        adapter = new CategoriaAdapter(getContext(), listaCategorias);
        recyclerCategorias.setAdapter(adapter);

        btnMisEmprendimientos = rootView.findViewById(R.id.btnMisEmprendimientos);
        btnMisEmprendimientos.setOnClickListener(this);

        // TODO: Llamar a tu API o BD para llenar categorías
        //cargarCategoriasDesdeBD();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnMisEmprendimientos){
            /*NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_emprendimiento_to_nav_emprendimiento_lista);*/
        }
    }

    /*private void cargarCategoriasDesdeBD() {
        String url = "https://tuservidor.com/api/categorias.php"; // Ajusta tu endpoint

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                listaCategorias.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id_categoria");
                        String nombre = obj.getString("nom_categoria");
                        String imagen = obj.getString("img_categoria");

                        listaCategorias.add(new Categoria(id, nombre, imagen));
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
}