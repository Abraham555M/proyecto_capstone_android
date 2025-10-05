package com.example.projectcapstone.ui.Emprendimiento;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.concurrent.TransferQueue;

import cz.msebera.android.httpclient.Header;

public class NuevoEmprendimientoFragment extends Fragment {

    private int idCategoria;
    private String nombreCategoria;
    private ImageView imgUpload;
    private ImageButton btnCamera;
    private EditText etNombreTienda;
    private EditText etDescripcion;
    private Button btnCrear;
    private int id_estudiante;

    private Uri imageUri; // para guardar la URI de la foto seleccionada

    // Lanzador para la cámara
    private ActivityResultLauncher<Intent> cameraLauncher;
    // Lanzador para galería
    private ActivityResultLauncher<String> galleryLauncher;

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nuevo_emprendimiento, container, false);

        if (getArguments() != null) {
            idCategoria = getArguments().getInt("id_categoria");
            nombreCategoria = getArguments().getString("nombre_categoria");
        }

        imgUpload = root.findViewById(R.id.imgUpload);
        btnCamera = root.findViewById(R.id.btnCamera);
        etNombreTienda = root.findViewById(R.id.etNombreTienda);
        etDescripcion = root.findViewById(R.id.etDescripcion);

        btnCrear = root.findViewById(R.id.btnCrear);
        btnCrear.setOnClickListener(v -> {
            String nombreTienda = etNombreTienda.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            SharedPreferences prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
            int idEstudiante = prefs.getInt("id_estudiante", -1);

            if (nombreTienda.isEmpty() || descripcion.isEmpty() || imageUri == null) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            agregarEmprendimiento(
                    idEstudiante,
                    idCategoria,
                    nombreTienda,
                    descripcion,
                    imageUri.getPath(),
                    imageUri.getPath()
            );
        });

        // Evento de cámara (foto nueva)
        btnCamera.setOnClickListener(v -> requestCameraPermission());

        // Evento de galería (si haces click en la imagen actual)
        imgUpload.setOnClickListener(v -> openGallery());


        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el launcher de cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (imageUri != null) {
                            Glide.with(this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_buscar)
                                    .error(R.drawable.ic_error)
                                    .into(imgUpload);
                        }
                    }
                });

        // Inicializamos el launcher de galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.ic_buscar) // mientras carga
                                .error(R.drawable.ic_error)        // si falla
                                .into(imgUpload);
                    }
                });
    }
    private void openCamera() {
        File photoFile = new File(
                requireActivity().getExternalFilesDir("Pictures"),
                "foto_" + System.currentTimeMillis() + ".jpg"
        );

        imageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                photoFile
        );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cameraLauncher.launch(intent);
    }
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera(); // si ya está concedido
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void agregarEmprendimiento(int idEstudiante, int idCategoria,
                                       String nombre, String descripcion,
                                       String imgPorEmprendimiento,
                                       String imgPerEmprendimiento) {

        String URL = ServidorConfig.URL_SERVIDOR + "emprendimiento/agregar_emprendimiento.php";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id_estudiante", idEstudiante);
        params.put("id_categoria", idCategoria);
        params.put("nom_emprendimiento", nombre);
        params.put("des_emprendimiento", descripcion);

        try {
            if (imgPorEmprendimiento != null) {
                File file1 = getFileFromUri(Uri.parse(imgPorEmprendimiento));
                if (file1 != null && file1.exists()) {
                    params.put("img_por_emprendimiento", file1);
                }
            }
            if (imgPerEmprendimiento != null) {
                File file2 = getFileFromUri(Uri.parse(imgPerEmprendimiento));
                if (file2 != null && file2.exists()) {
                    params.put("img_per_emprendimiento", file2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Toast.makeText(getContext(), "Éxito: " + response, Toast.LENGTH_LONG).show();

                NavController navController = Navigation.findNavController(requireView());
                navController.popBackStack();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMsg = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            // Abrir input stream desde el content resolver
            Context context = requireContext();
            String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
            File cacheDir = context.getCacheDir();
            file = new File(cacheDir, fileName);

            try (java.io.InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 java.io.OutputStream outputStream = new java.io.FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}