package com.example.projectcapstone;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

        handleIntent(getIntent());
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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "❌ Error al obtener el token", task.getException());
                        return;
                    }
                    // Obtener token
                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "🔥 Token actual: " + token);
                });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getStringExtra("NAVIGATE_TO_ACTION");
        String postId = intent.getStringExtra("NAVIGATE_TO_POST_ID");
        String emprendimientoId = intent.getStringExtra("NAVIGATE_TO_EMPRENDIMIENTO_ID");

        if ("OPEN_POST_DETAIL".equals(action) && (postId != null || emprendimientoId != null)) {
            Log.d("FCM_NAV", "Redirigiendo a emprendimiento ID: " + emprendimientoId + " (Publicación: " + postId + ")");

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            Bundle bundle = new Bundle();

            if (emprendimientoId != null) {
                bundle.putString("id_emprendimiento", emprendimientoId);
            } else if (postId != null) {
                bundle.putString("id_publicacion", postId);
            }

            try {
                navController.navigate(R.id.nav_perfil_emprendedor, bundle);
            } catch (Exception e) {
                Log.e("FCM_NAV", "Error al navegar: " + e.getMessage());
            }

            intent.removeExtra("NAVIGATE_TO_ACTION");
            intent.removeExtra("NAVIGATE_TO_POST_ID");
            intent.removeExtra("NAVIGATE_TO_EMPRENDIMIENTO_ID");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ... (Tu código se mantiene igual) ...
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
            mostrarDialogoConfiguracionNotificaciones();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoConfiguracionNotificaciones() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.alert_dialog_configuracion_notificaciones);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        // Referencias de vistas
        ImageView btnCerrar = dialog.findViewById(R.id.btnCerrarNotificaciones);
        MaterialButton btnCancelar = dialog.findViewById(R.id.btnCancelarNotificaciones);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarNotificaciones);

        SwitchMaterial switchTodas = dialog.findViewById(R.id.switchTodasNotificaciones);
        SwitchMaterial switchPublicaciones = dialog.findViewById(R.id.switchPublicaciones);
        SwitchMaterial switchComentarios = dialog.findViewById(R.id.switchComentarios);
        SwitchMaterial switchLikes = dialog.findViewById(R.id.switchLikes);

        // Deshabilitar botón de guardar hasta que carguen los datos
        btnGuardar.setEnabled(false);

        // --- Lógica de Sincronización del Switch "Todas" ---
        switchTodas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchPublicaciones.setChecked(isChecked);
            switchComentarios.setChecked(isChecked);
            switchLikes.setChecked(isChecked);
        });

        // --- Cargar configuraciones actuales desde el servidor ---
        cargarConfiguracionActual(switchPublicaciones, switchComentarios, switchLikes, btnGuardar);

        // Acciones de botones
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            // Recolectar valores
            boolean publicaciones = switchPublicaciones.isChecked();
            boolean comentarios = switchComentarios.isChecked();
            boolean likes = switchLikes.isChecked();

            // Enviar al servidor
            guardarConfiguracionServidor(publicaciones, comentarios, likes, dialog);
        });

        dialog.show();
    }
    private void cargarConfiguracionActual(SwitchMaterial switchPublicaciones, SwitchMaterial switchComentarios, SwitchMaterial switchLikes, MaterialButton btnGuardar) {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "notificacion/configuracion_leer.php?idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if ("success".equals(json.getString("status"))) {
                        JSONObject config = json.getJSONObject("config");

                        switchPublicaciones.setChecked(config.getBoolean("publicaciones"));
                        switchComentarios.setChecked(config.getBoolean("comentarios"));
                        switchLikes.setChecked(config.getBoolean("likes"));

                        btnGuardar.setEnabled(true);
                    } else {
                        Toast.makeText(MainActivity.this, "Error al leer config: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("CONFIG_NOTIF", "Error de parsing (Lectura): " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error de parsing (Lectura)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("CONFIG_NOTIF", "Error de conexión (Lectura): " + statusCode);
                Toast.makeText(MainActivity.this, "Error de conexión al leer config", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarConfiguracionServidor(boolean publicaciones, boolean comentarios, boolean likes, Dialog dialog) {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "notificacion/configuracion_guardar.php";

        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("publicaciones", String.valueOf(publicaciones)); // "true" o "false"
        params.put("comentarios", String.valueOf(comentarios));
        params.put("likes", String.valueOf(likes));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if ("success".equals(json.getString("status"))) {
                        Toast.makeText(MainActivity.this, "Configuración guardada", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al guardar: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("CONFIG_NOTIF", "Error de parsing (Guardado): " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error de parsing (Guardado)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("CONFIG_NOTIF", "Error de conexión (Guardado): " + statusCode);
                Toast.makeText(MainActivity.this, "Error de conexión al guardar", Toast.LENGTH_SHORT).show();
            }
        });
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
                    /*
                    if (jsonObject.has("error")) {
                        Toast.makeText(MainActivity.this, "No se encontró el estudiante", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    */
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