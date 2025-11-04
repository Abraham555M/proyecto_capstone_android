package com.example.projectcapstone;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectcapstone.databinding.ActivityMainBinding;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SessionManager session;

    private TextView tvNombreEstudiante;
    private ImageView imageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);

        // -------------------------------
        // 🔹 Configuración de navegación
        // -------------------------------
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavInflater navInflater = navController.getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.mobile_navigation);

        if (session.isSesionActiva()) {
            Log.d("SESSION_MANAGER", "✅ Sesión activa detectada: " + session.getNombre());
            navGraph.setStartDestination(R.id.nav_inicio);
        } else {
            Log.d("SESSION_MANAGER", "⚠️ No hay sesión activa. Dirigiendo a StartUpn");
            navGraph.setStartDestination(R.id.nav_start_upn);
        }

        navController.setGraph(navGraph);
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_metricas, R.id.nav_emprendimiento, R.id.nav_publicaciones,
                R.id.nav_inicio, R.id.nav_notificaciones, R.id.nav_colaboraciones,
                R.id.nav_favoritos, R.id.nav_perfil, R.id.nav_soporte, R.id.nav_administrador)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // -------------------------------
        // 🔹 Control de visibilidad ADMIN
        // -------------------------------
        navigationView.post(() -> {
            int tipoUsuario = session.getTipoUsuario();
            Log.d("TIPO_USUARIO", "Tipo de usuario: " + tipoUsuario);

            MenuItem itemAdmin = navigationView.getMenu().findItem(R.id.nav_administrador);
            if (itemAdmin != null) {
                if (tipoUsuario == 2) {
                    itemAdmin.setVisible(true);
                    Log.d("MENU", "🧩 Usuario administrador — menú visible");
                } else {
                    itemAdmin.setVisible(false);
                    Log.d("MENU", "🔒 Usuario normal — menú oculto");
                }
            } else {
                Log.e("MENU", "❌ No se encontró el ítem nav_administrador en el menú");
            }
        });

        // -------------------------------
        // 🔹 Header del Drawer
        // -------------------------------
        View headerView = navigationView.getHeaderView(0);
        tvNombreEstudiante = headerView.findViewById(R.id.tvNombreEstudiante);
        imageViewProfile = headerView.findViewById(R.id.imageViewProfile);

        cargarInformacionPerfil();

        // -------------------------------
        // 🔹 Control de toolbar y drawer
        // -------------------------------
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_crear_cuenta ||
                    destination.getId() == R.id.nav_inicio_sesion ||
                    destination.getId() == R.id.nav_validar_correo_crear ||
                    destination.getId() == R.id.nav_start_upn ||
                    destination.getId() == R.id.nav_confirmar_password ||
                    destination.getId() == R.id.nav_validar_correo_recuperar ||
                    destination.getId() == R.id.nav_cambiar_password) {

                binding.appBarMain.toolbar.setVisibility(View.GONE);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            session.cerrarSesion();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), true)
                    .build();

            navController.navigate(R.id.nav_start_upn, null, navOptions);
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_notificaciones) {
            // Crear el diálogo
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.alert_dialog_configuracion_notificaciones);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);

            // Referencias de vistas dentro del diálogo
            ImageView btnCerrar = dialog.findViewById(R.id.btnCerrarNotificaciones);
            MaterialButton btnCancelar = dialog.findViewById(R.id.btnCancelarNotificaciones);
            MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarNotificaciones);

            SwitchMaterial switchTodas = dialog.findViewById(R.id.switchTodasNotificaciones);
            SwitchMaterial switchPublicaciones = dialog.findViewById(R.id.switchPublicaciones);
            SwitchMaterial switchComentarios = dialog.findViewById(R.id.switchComentarios);
            SwitchMaterial switchLikes = dialog.findViewById(R.id.switchLikes);

            // Acciones
            btnCerrar.setOnClickListener(v -> dialog.dismiss());
            btnCancelar.setOnClickListener(v -> dialog.dismiss());

            btnGuardar.setOnClickListener(v -> {
                boolean todas = switchTodas.isChecked();
                boolean publicaciones = switchPublicaciones.isChecked();
                boolean comentarios = switchComentarios.isChecked();
                boolean likes = switchLikes.isChecked();

                Toast.makeText(MainActivity.this,
                        "Configuración guardada:\n" +
                                "Todas: " + todas + "\n" +
                                "Publicaciones: " + publicaciones + "\n" +
                                "Comentarios: " + comentarios + "\n" +
                                "Likes: " + likes,
                        Toast.LENGTH_LONG).show();

                dialog.dismiss();
            });

            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarInformacionPerfil() {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_informacion_perfil.php?idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(respuesta);

                    if (jsonObject.has("error")) {
                        Toast.makeText(MainActivity.this, "No se encontró el estudiante", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String nombre = jsonObject.optString("nombre", "Sin nombre");
                    tvNombreEstudiante.setText(nombre);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar los datos del perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Error al cargar información del perfil", Toast.LENGTH_SHORT).show();
                Log.e("PERFIL", "Error HTTP: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
