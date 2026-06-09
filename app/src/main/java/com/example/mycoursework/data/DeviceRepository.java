package com.example.mycoursework.data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.mycoursework.model.AcSettings;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DeviceRepository {
    private static DeviceRepository instance;
    private final DeviceDao deviceDao;
    private final RoomDao roomDao;
    private final AcSettingsDao acSettingsDao;
    private final ExecutorService executor;
    private final MutableLiveData<String> currentUsername = new MutableLiveData<>();

    private DeviceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        deviceDao = db.deviceDao();
        roomDao = db.roomDao();
        acSettingsDao = db.acSettingsDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public static synchronized DeviceRepository getInstance(Application application) {
        if (instance == null) {
            instance = new DeviceRepository(application);
        }
        return instance;
    }

    public void setCurrentUser(String username) {
        currentUsername.setValue(username);
    }

    public LiveData<List<Device>> getDevices() {
        return Transformations.switchMap(currentUsername, username -> {
            if (username == null) return new MutableLiveData<>(new ArrayList<>());
            return deviceDao.getDevicesForUser(username);
        });
    }

    public LiveData<List<Room>> getRooms() {
        return Transformations.switchMap(currentUsername, username -> {
            if (username == null) return new MutableLiveData<>(new ArrayList<>());
            return roomDao.getRoomsForUser(username);
        });
    }

    public void addDevice(String deviceName, String roomName, Device.Type type, int imageResId) {
        executor.execute(() -> {
            String username = currentUsername.getValue();
            if (username == null) return;

            Room room = roomDao.getRoomByNameAndUser(username, roomName);
            String roomId;
            if (room == null) {
                Room newRoom = new Room(roomName, username);
                roomDao.insert(newRoom);
                roomId = newRoom.getId();
            } else {
                roomId = room.getId();
            }

            Device device = new Device(deviceName, roomId, type, imageResId);
            deviceDao.insert(device);

            if (type == Device.Type.AC) {
                acSettingsDao.insert(new AcSettings(device.getId(), 22));
            }
        });
    }

    public void updateDevice(Device updatedDevice) {
        executor.execute(() -> deviceDao.update(updatedDevice));
    }

    public void updateAcTemperature(String deviceId, int temperature) {
        executor.execute(() -> {
            // Update AcSettings
            AcSettings settings = acSettingsDao.getSettingsForDeviceSync(deviceId);
            if (settings != null) {
                settings.setTemperature(temperature);
                acSettingsDao.update(settings);
            } else {
                acSettingsDao.insert(new AcSettings(deviceId, temperature));
            }

            // Update Device entity to show temperature in the list
            Device device = deviceDao.getDeviceById(deviceId);
            if (device != null) {
                device.setTemperature(temperature);
                deviceDao.update(device);
            }
        });
    }

    public LiveData<AcSettings> getAcSettings(String deviceId) {
        return acSettingsDao.getSettingsForDevice(deviceId);
    }

    public void deleteDevice(String deviceId) {
        executor.execute(() -> deviceDao.deleteById(deviceId));
    }

    public interface DeviceCallback {
        void onDeviceLoaded(Device device);
    }

    public void getDeviceById(String deviceId, DeviceCallback callback) {
        executor.execute(() -> {
            Device device = deviceDao.getDeviceById(deviceId);
            callback.onDeviceLoaded(device);
        });
    }
}
