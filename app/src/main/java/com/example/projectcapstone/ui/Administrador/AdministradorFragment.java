package com.example.projectcapstone.ui.Administrador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Administrador.Adapter.AdminPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdministradorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administrador, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutAdmin);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerAdmin);

        AdminPagerAdapter adapter = new AdminPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Reportes"); break;
                case 1: tab.setText("Dashboards"); break;
                case 2: tab.setText("Solicitudes"); break;
            }
        }).attach();

        return view;
    }
}