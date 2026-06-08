package com.example.mycoursework.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mycoursework.R;
import com.example.mycoursework.data.DeviceRepository;
import com.example.mycoursework.model.Device;

import java.util.List;

public class TimerReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "device_timer_channel";
    public static final String EXTRA_DEVICE_ID = "device_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String deviceId = intent.getStringExtra(EXTRA_DEVICE_ID);
        if (deviceId == null) return;

        createNotificationChannel(context);

        // Find the device to get its name
        DeviceRepository repository = DeviceRepository.getInstance();
        List<Device> devices = repository.getDevices().getValue();
        Device targetDevice = null;
        if (devices != null) {
            for (Device d : devices) {
                if (d.getId().equals(deviceId)) {
                    targetDevice = d;
                    break;
                }
            }
        }

        if (targetDevice != null) {
            // Update device state: turn off and reset timer
            targetDevice.setEnabled(false);
            targetDevice.setTimerActive(false);
            repository.updateDevice(targetDevice);

            showNotification(context, targetDevice.getName());
        }
    }

    private void showNotification(Context context, String deviceName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_light)
                .setContentTitle("Timer Finished")
                .setContentText("The timer for " + deviceName + " has ended and it has been turned off.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Using deviceId hash as notification id or just a random one
        notificationManager.notify(deviceName.hashCode(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Device Timers";
            String description = "Notifications for finished device timers";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
