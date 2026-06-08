package com.example.mycoursework.ui.details;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentDeviceDetailsBinding;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.notification.TimerReceiver;
import com.example.mycoursework.viewmodel.DeviceViewModel;

public class DeviceDetailsFragment extends Fragment {

    private FragmentDeviceDetailsBinding binding;
    private DeviceViewModel viewModel;
    private Device device;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

        if (getArguments() != null) {
            Device d = getArguments().getParcelable("device");
            if (d != null) {
                deviceId = d.getId();
            }
        }

        if (deviceId != null) {
            viewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
                for (Device d : devices) {
                    if (d.getId().equals(deviceId)) {
                        this.device = d;
                        updateUI();
                        break;
                    }
                }
            });
            setupListeners();
        }

        binding.buttonDeleteDevice.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupListeners() {
        binding.switchDetailEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (device != null && device.isEnabled() != isChecked) {
                Device updated = device.copy();
                updated.setEnabled(isChecked);
                
                // Логика: если кондиционер выключается, отменяем таймер
                if (updated.getType() == Device.Type.AC && !isChecked && updated.isTimerActive()) {
                    cancelTimerInternal(updated);
                }
                
                viewModel.updateDevice(updated);
            }
        });

        binding.seekbarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) binding.textTempValue.setText(progress + "°C");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Device updated = device.copy();
                updated.setTemperature(seekBar.getProgress());
                viewModel.updateDevice(updated);
            }
        });

        binding.buttonStartTimer.setOnClickListener(v -> {
            try {
                int minutes = Integer.parseInt(binding.editTimerValue.getText().toString());
                if (minutes > 0) {
                    startTimer(minutes);
                } else {
                    Toast.makeText(requireContext(), "Введите время > 0", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректное число", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonStopTimer.setOnClickListener(v -> {
            Device updated = device.copy();
            cancelTimerInternal(updated);
            viewModel.updateDevice(updated);
            Toast.makeText(requireContext(), "Таймер остановлен", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        if (device == null) return;
        
        binding.textDetailName.setText(device.getName());
        binding.textDetailRoom.setText(device.getRoom());
        binding.imageDetailDevice.setImageResource(device.getImageResId());
        
        binding.switchDetailEnabled.setOnCheckedChangeListener(null);
        binding.switchDetailEnabled.setChecked(device.isEnabled());
        binding.switchDetailEnabled.setText(device.isEnabled() ? R.string.label_on : R.string.label_off);
        binding.switchDetailEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Device updated = device.copy();
            updated.setEnabled(isChecked);
            if (updated.getType() == Device.Type.AC && !isChecked && updated.isTimerActive()) {
                cancelTimerInternal(updated);
            }
            viewModel.updateDevice(updated);
        });

        if (device.getType() == Device.Type.AC) {
            binding.layoutAcControls.setVisibility(View.VISIBLE);
            binding.seekbarTemp.setProgress(device.getTemperature());
            binding.textTempValue.setText(device.getTemperature() + "°C");
        } else {
            binding.layoutAcControls.setVisibility(View.GONE);
        }

        if (device.getType() == Device.Type.SOCKET) {
            binding.layoutTimerControls.setVisibility(View.GONE);
        } else {
            binding.layoutTimerControls.setVisibility(View.VISIBLE);
        }

        if (!binding.editTimerValue.hasFocus()) {
            binding.editTimerValue.setText(String.valueOf(device.getTimerMinutes()));
        }
        
        binding.buttonStopTimer.setEnabled(device.isTimerActive());
    }

    private void startTimer(int minutes) {
        Device updated = device.copy();
        updated.setTimerMinutes(minutes);
        updated.setTimerStartTime(System.currentTimeMillis());
        updated.setTimerActive(true);
        viewModel.updateDevice(updated);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), TimerReceiver.class);
        intent.putExtra(TimerReceiver.EXTRA_DEVICE_ID, device.getId());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 
                device.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + (long) minutes * 60 * 1000;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        
        Toast.makeText(requireContext(), "Таймер запущен", Toast.LENGTH_SHORT).show();
    }

    private void cancelTimerInternal(Device updated) {
        updated.setTimerActive(false);
        updated.setTimerMinutes(0);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 
                updated.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_device_details)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (deviceId != null) {
                        viewModel.deleteDevice(deviceId);
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
