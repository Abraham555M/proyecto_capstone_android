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

import com.example.projectcapstone.R;

public class EmprendimientoFragment extends Fragment implements View.OnClickListener{
    private Button btnMisEmprendimientos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emprendimiento, container, false);

        btnMisEmprendimientos = rootView.findViewById(R.id.btnMisEmprendimientos);
        btnMisEmprendimientos.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btnMisEmprendimientos){
            /*NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_emprendimiento_to_nav_emprendimiento_lista);*/
        }
    }
}