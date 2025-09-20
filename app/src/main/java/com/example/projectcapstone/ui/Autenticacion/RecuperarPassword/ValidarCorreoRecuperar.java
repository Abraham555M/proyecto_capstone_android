package com.example.projectcapstone.ui.Autenticacion.RecuperarPassword;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectcapstone.R;

public class ValidarCorreoRecuperar extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validar_correo_recuperar, container, false);

        return rootView;
    }
}