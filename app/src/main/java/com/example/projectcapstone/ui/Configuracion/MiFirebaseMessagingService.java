package com.example.projectcapstone.ui.Configuracion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.projectcapstone.MainActivity;
import com.example.projectcapstone.R; // Asegúrate de que este import sea correcto
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_SERVICE";
    private static final String CHANNEL_ID = "CapZone_Channel";

    /**
     * Se llama cuando se recibe un mensaje FCM.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Mensaje recibido ID: " + remoteMessage.getMessageId());

        String title = "Nueva Notificación";
        String body = "Tienes nueva actividad.";
        String postId = null;

        // 1. Extraer Notificación
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notificación: " + title + " / " + body);
        }

        // 2. Manejar los datos (Payload)
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Datos recibidos: " + data);

            // Extraemos el ID de la publicación que enviamos desde PHP
            postId = data.get("id_publicacion");
        }

        // 3. Mostrar la notificación con el PendingIntent de navegación
        showNotification(title, body, postId);
    }

    /**
     * Muestra la notificación y prepara el PendingIntent para la navegación.
     */
    private void showNotification(String title, String body, String postId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent que se disparará al tocar la notificación (abre MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 🚨 Adjuntar el ID de la publicación al Intent para que MainActivity lo lea
        if (postId != null) {
            intent.putExtra("NAVIGATE_TO_POST_ID", postId);
            intent.putExtra("NAVIGATE_TO_ACTION", "OPEN_POST");
        }

        // Configuración de PendingIntent (CRÍTICO: FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT)
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0, // Request code
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // 1. Crear Canal con ALTA PRIORIDAD (Android 8.0 / API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de CapZone",
                    NotificationManager.IMPORTANCE_HIGH); // IMPORTANCE_HIGH para Heads-up

            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // 2. Construir la notificación
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // Asegúrate de que exista
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent) // 🚨 Asignar el Intent
                        .setAutoCancel(true);

        // 🚨 PARA VERSIONES ANTIGUAS (<= Android O): Establecer alta prioridad para Heads-up
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        }

        notificationManager.notify(0 /* ID de notificación */, notificationBuilder.build());
    }

    /**
     * Se llama si el token de registro se actualiza.
     * Aquí debes llamar a tu sendRegistrationToServer.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Nuevo token de registro: " + token);
        // Aquí debería estar la llamada a sendRegistrationToServer(studentId, token)
    }
}