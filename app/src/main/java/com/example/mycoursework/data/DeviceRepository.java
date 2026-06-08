package com.example.mycoursework.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.mycoursework.model.Device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceRepository {
    private static DeviceRepository instance;
    private final MutableLiveData<List<Device>> devicesLiveData = new MutableLiveData<>(new ArrayList<>());

    private DeviceRepository() {
        List<Device> initialDevices = new ArrayList<>();
        initialDevices.add(new Device("Main Light", "Living Room", Device.Type.LIGHT));
        initialDevices.add(new Device("AC", "Bedroom", Device.Type.AC));
        initialDevices.add(new Device("Smart Plug", "Kitchen", Device.Type.SOCKET));
        devicesLiveData.setValue(initialDevices);
    }

    public static synchronized DeviceRepository getInstance() {
        if (instance == null) {
            instance = new DeviceRepository();
        }
        return instance;
    }

    public LiveData<List<Device>> getDevices() {
        return devicesLiveData;
    }

    public LiveData<List<String>> getRooms() {
        return Transformations.map(devicesLiveData, devices -> {
            Set<String> rooms = new HashSet<>();
            for (Device device : devices) {
                rooms.add(device.getRoom());
            }
            return new ArrayList<>(rooms);
        });
    }

    public void addDevice(Device device) {
        List<Device> currentDevices = new ArrayList<>(devicesLiveData.getValue());
        currentDevices.add(device);
        devicesLiveData.setValue(currentDevices);
    }

    public void updateDevice(Device updatedDevice) {
        List<Device> currentDevices = new ArrayList<>(devicesLiveData.getValue());
        for (int i = 0; i < currentDevices.size(); i++) {
            if (currentDevices.get(i).getId().equals(updatedDevice.getId())) {
                currentDevices.set(i, updatedDevice);
                break;
            }
        }
        devicesLiveData.setValue(currentDevices);
    }

    public void turnOffAllDevices() {
        List<Device> currentDevices = new ArrayList<>(devicesLiveData.getValue());
        boolean changed = false;
        for (Device device : currentDevices) {
            if (device.isEnabled()) {
                device.setEnabled(false);
                device.setTimerActive(false);
                changed = true;
            }
        }
        if (changed) {
            devicesLiveData.setValue(currentDevices);
        }
    }

    public void deleteDevice(String deviceId) {
        List<Device> currentDevices = new ArrayList<>(devicesLiveData.getValue());
        currentDevices.removeIf(device -> device.getId().equals(deviceId));
        devicesLiveData.setValue(currentDevices);
    }
}
