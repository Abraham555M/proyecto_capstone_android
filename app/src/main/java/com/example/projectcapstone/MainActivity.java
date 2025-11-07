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
import android.content.Intent; // Necesario para Intent


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

        // 🚨 CRÍTICO: Manejar el intent al CREAR la actividad
        handleIntent(getIntent());

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 🚨 Manejar el intent si la app ya estaba abierta y recibe una notificación
        handleIntent(intent);
    }

    /**
     * Lógica de redirección cuando la actividad se lanza o se reanuda desde una notificación.
     */
    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        // Claves definidas en MiFirebaseMessagingService.java
        String action = intent.getStringExtra("NAVIGATE_TO_ACTION");
        String postId = intent.getStringExtra("NAVIGATE_TO_POST_ID");
        String emprendimientoId = intent.getStringExtra("NAVIGATE_TO_EMPRENDIMIENTO_ID");

        if ("OPEN_POST_DETAIL".equals(action) && (postId != null || emprendimientoId != null)) {
            Log.d("FCM_NAV", "Redirigiendo a emprendimiento ID: " + emprendimientoId + " (Publicación: " + postId + ")");

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            Bundle bundle = new Bundle();

            // Pasamos los IDs necesarios para que el fragmento receptor los maneje
            if (emprendimientoId != null) {
                bundle.putString("id_emprendimiento", emprendimientoId);
            } else if (postId != null) {
                // Si solo tenemos el PostId, lo enviamos. El fragmento debe manejar la carga.
                bundle.putString("id_publicacion", postId);
            }

            try {
                // ✅ Usamos el destino existente que muestra el perfil del emprendedor
                navController.navigate(R.id.nav_perfil_emprendedor, bundle);
            } catch (Exception e) {
                // Esto te ayudará a diagnosticar si el fragmento no está recibiendo los argumentos correctamente
                Log.e("FCM_NAV", "Error al navegar: Asegúrate que el fragmento 'nav_perfil_emprendedor' exista y espere los argumentos. " + e.getMessage());
            }

            // Limpiar Intent para evitar la doble navegación
            intent.removeExtra("NAVIGATE_TO_ACTION");
            intent.removeExtra("NAVIGATE_TO_POST_ID");
            intent.removeExtra("NAVIGATE_TO_EMPRENDIMIENTO_ID");
        }
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
        // ... (Tu código existente para el menú) ...
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
