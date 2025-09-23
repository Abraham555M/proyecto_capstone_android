package com.example.projectcapstone.ui.Emprendimiento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectcapstone.R;

public class EmprendimientoLista extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emprendimiento_lista, container, false);

        return rootView;
    }
}