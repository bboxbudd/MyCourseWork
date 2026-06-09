package com.example.mycoursework.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mycoursework.data.DeviceRepository;
import com.example.mycoursework.model.AcSettings;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.model.Room;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {
    private final DeviceRepository repository;
    private final LiveData<List<Device>> devices;
    private final LiveData<List<Room>> rooms;
    private final MutableLiveData<String> selectedRoom = new MutableLiveData<>();

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        repository = DeviceRepository.getInstance(application);
        devices = repository.getDevices();
        rooms = repository.getRooms();
    }

    public void setCurrentUser(String username) {
        repository.setCurrentUser(username);
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }

    public LiveData<List<Room>> getRooms() {
        return rooms;
    }

    public void setSelectedRoom(String roomName) {
        selectedRoom.setValue(roomName);
    }

    public LiveData<String> getSelectedRoom() {
        return selectedRoom;
    }

    public void addDevice(String name, String room, Device.Type type, int imageResId) {
        repository.addDevice(name, room, type, imageResId);
    }

    public void updateDevice(Device device) {
        repository.updateDevice(device);
    }

    public void deleteDevice(String deviceId) {
        repository.deleteDevice(deviceId);
    }

    public LiveData<AcSettings> getAcSettings(String deviceId) {
        return repository.getAcSettings(deviceId);
    }

    public void updateAcTemperature(String deviceId, int temperature) {
        repository.updateAcTemperature(deviceId, temperature);
    }
}
