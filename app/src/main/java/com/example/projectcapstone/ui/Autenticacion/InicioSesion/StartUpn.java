package com.example.projectcapstone.ui.Autenticacion.InicioSesion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.projectcapstone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StartUpn extends Fragment {

    private Button btncom;
    private FloatingActionButton btnres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        btncom = (Button) rootView.findViewById(R.id.btnComenzar);
        btnres = (FloatingActionButton) rootView.findViewById(R.id.btnRegistrar);

        btncom.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(R.id.action_nav_start_upn_to_nav_inicio_sesion);
        });

        btnres.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(rootView);
            navController.navigate(R.id.action_nav_start_upn_to_nav_crear_cuenta);
        });

        return rootView;
    }
}