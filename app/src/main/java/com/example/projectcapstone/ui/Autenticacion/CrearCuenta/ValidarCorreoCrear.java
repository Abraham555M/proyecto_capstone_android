package com.example.projectcapstone.ui.Autenticacion.CrearCuenta;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ValidarCorreoCrear extends Fragment {

    private EditText etDigit1, etDigit2, etDigit3, etDigit4;
    private MaterialButton btnValidar;
    private TextView tvCancelar;

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_validar_correo_crear, container, false);

       // Referencias
       etDigit1 = rootView.findViewById(R.id.etDigit1);
       etDigit2 = rootView.findViewById(R.id.etDigit2);
       etDigit3 = rootView.findViewById(R.id.etDigit3);
       etDigit4 = rootView.findViewById(R.id.etDigit4);
       btnValidar = rootView.findViewById(R.id.btnValidar);
       tvCancelar = rootView.findViewById(R.id.tvCancelar);

       String correo = getArguments().getString("correo");

       // Mover cursor automáticamente al siguiente campo
       setupOtpInputs();

       // Acción del botón validar
       btnValidar.setOnClickListener(v -> {
           String codigo = etDigit1.getText().toString() +
                   etDigit2.getText().toString() +
                   etDigit3.getText().toString() +
                   etDigit4.getText().toString();

           if (codigo.length() == 4) {
               // Aquí validas el código con tu backend
               validarCodigoEnServidor(correo, codigo);
           } else {
               Toast.makeText(getContext(), "Debes ingresar los 4 dígitos", Toast.LENGTH_SHORT).show();
           }
       });

       // Acción de cancelar
       tvCancelar.setOnClickListener(v -> {
           requireActivity().onBackPressed(); // Regresa al fragmento anterior
       });

        return rootView;
   }

    private void validarCodigoEnServidor(String mail, String codigo) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", mail);
        params.put("codigo", codigo);

        // URL de tu endpoint en el backend que valida el código
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/verificar_codigo.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                if (response.equalsIgnoreCase("OK")) {
                    // Inflar tu layout personalizado
                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                    View dialogView = inflater.inflate(R.layout.alert_dialog_res_positiva, null);

                    // Referencias a los elementos del layout
                    TextView tvTitulo = dialogView.findViewById(R.id.tvTituloExito);
                    TextView tvMensaje = dialogView.findViewById(R.id.tvMensajeExito);
                    MaterialButton btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);

                    // Cambiar dinámicamente título y mensaje
                    tvTitulo.setText("Validación Exitosa");
                    tvMensaje.setText("Tu cuenta fue activada correctamente 🎉");

                    // Crear el diálogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setView(dialogView);

                    // Evitar que se cierre tocando afuera
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);

                    // Acción del botón
                    btnAceptar.setOnClickListener(v -> {
                        alertDialog.dismiss();
                        // Navegar al inicio
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_validar_correo_crear_to_nav_inicio);
                    });

                    // Mostrar el diálogo
                    alertDialog.show();;
                } else {
                    Toast.makeText(getContext(), "Código incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de la verificacion de correo" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        // Cuando se llena un dígito pasa al siguiente
        etDigit1.addTextChangedListener(new GenericTextWatcher(etDigit1, etDigit2));
        etDigit2.addTextChangedListener(new GenericTextWatcher(etDigit2, etDigit3));
        etDigit3.addTextChangedListener(new GenericTextWatcher(etDigit3, etDigit4));
        // Si llegas al 4to, no salta más
    }

    private class GenericTextWatcher implements TextWatcher {

        private View currentView;
        private View nextView;

        public GenericTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}