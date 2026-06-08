package com.example.mycoursework.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mycoursework.data.DeviceRepository;
import com.example.mycoursework.model.Device;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {
    private final DeviceRepository repository;
    private final LiveData<List<Device>> devices;
    private final LiveData<List<String>> rooms;
    private final MutableLiveData<String> selectedRoom = new MutableLiveData<>();

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        repository = DeviceRepository.getInstance();
        devices = repository.getDevices();
        rooms = repository.getRooms();
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }

    public LiveData<List<String>> getRooms() {
        return rooms;
    }

    public void setSelectedRoom(String roomName) {
        selectedRoom.setValue(roomName);
    }

    public LiveData<String> getSelectedRoom() {
        return selectedRoom;
    }

    public void addDevice(Device device) {
        repository.addDevice(device);
    }

    public void updateDevice(Device device) {
        repository.updateDevice(device);
    }

    public void turnOffAllDevices() {
        repository.turnOffAllDevices();
    }

    public void deleteDevice(String deviceId) {
        repository.deleteDevice(deviceId);
    }
}
