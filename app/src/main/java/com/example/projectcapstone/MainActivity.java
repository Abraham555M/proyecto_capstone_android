package com.example.projectcapstone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcapstone.databinding.ActivityMainBinding;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Acción rápida", Snackbar.LENGTH_LONG)
                        .setAction("Ok", null)
                        .setAnchorView(R.id.fab)
                        .show()
        );

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_metricas, R.id.nav_emprendimiento, R.id.nav_publicaciones,
                R.id.nav_inicio, R.id.nav_notificaciones, R.id.nav_colaboraciones,
                R.id.nav_favoritos, R.id.nav_perfil, R.id.nav_soporte)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        session = new SessionManager(this);
        if (!session.isSesionActiva()) {
            // Navegar al fragmento de inicio de sesión después de que el NavHost esté listo
            binding.getRoot().post(() -> {
                NavController navControllere = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(navControllere.getGraph().getStartDestinationId(), true)
                        .build();
                navControllere.navigate(R.id.nav_inicio, null, navOptions);
            });
        }

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_crear_cuenta ||
                    destination.getId() == R.id.nav_inicio_sesion ||
                    destination.getId() == R.id.nav_validar_correo_crear ||
                    destination.getId() == R.id.nav_start_upn ||
                    destination.getId() == R.id.nav_confirmar_password ||
                    destination.getId() == R.id.nav_validar_correo_recuperar ||
                    destination.getId() == R.id.nav_cambiar_password) {

                binding.appBarMain.toolbar.setVisibility(View.GONE); // Quitar el encabezado
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // Desactiva swipe
                binding.appBarMain.fab.setVisibility(View.GONE); //Quitar el flotante
            } else {
                binding.appBarMain.toolbar.setVisibility(View.VISIBLE); // Reactivar el encabezado
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); // Reactiva swipe
                binding.appBarMain.fab.setVisibility(View.VISIBLE); // Reactivar el flotante
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            Log.w("MainActivity", "No se pudo mostrar íconos en menú: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}