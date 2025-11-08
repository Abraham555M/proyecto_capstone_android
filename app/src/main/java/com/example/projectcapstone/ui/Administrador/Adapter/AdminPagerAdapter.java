package com.example.projectcapstone.ui.Administrador.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectcapstone.ui.Administrador.Fragments.DashboardFragment;
import com.example.projectcapstone.ui.Administrador.Fragments.ReportesFragment;
import com.example.projectcapstone.ui.Administrador.Fragments.SolicitudesFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {
    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ReportesFragment();
            case 1: return new DashboardFragment();
            case 2: return new SolicitudesFragment();
            default: return new ReportesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
