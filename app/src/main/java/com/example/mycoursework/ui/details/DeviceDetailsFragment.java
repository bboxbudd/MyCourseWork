package com.example.mycoursework.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.data.AppDatabase;
import com.example.mycoursework.databinding.FragmentDeviceDetailsBinding;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.viewmodel.DeviceViewModel;
import com.example.mycoursework.viewmodel.LoginViewModel;

public class DeviceDetailsFragment extends Fragment {

    private FragmentDeviceDetailsBinding binding;
    private DeviceViewModel viewModel;
    private LoginViewModel loginViewModel;
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
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        if (getArguments() != null) {
            Device d = getArguments().getParcelable("device");
            if (d != null) {
                deviceId = d.getId();
            }
        }

        if (deviceId != null) {
            viewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
                if (devices != null) {
                    for (Device d : devices) {
                        if (d.getId().equals(deviceId)) {
                            this.device = d;
                            updateUI();
                            break;
                        }
                    }
                }
            });
            setupListeners();
        }

        loginViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.buttonDeleteDevice.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonDeleteDevice.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupListeners() {
        binding.switchDetailEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (device != null && device.isEnabled() != isChecked) {
                Device updated = device.copy();
                updated.setEnabled(isChecked);
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
                if (device != null) {
                    viewModel.updateAcTemperature(device.getId(), seekBar.getProgress());
                }
            }
        });
    }

    private void updateUI() {
        if (device == null) return;
        
        binding.textDetailName.setText(device.getName());
        
        AppDatabase.getDatabase(requireContext()).roomDao().getRoomNameById(device.getRoomId())
                .observe(getViewLifecycleOwner(), roomName -> {
                    if (roomName != null) {
                        binding.textDetailRoom.setText(roomName);
                    }
                });

        binding.imageDetailDevice.setImageResource(device.getImageResId());
        
        binding.switchDetailEnabled.setOnCheckedChangeListener(null);
        switchEnabledInDetails(device.isEnabled());

        if (device.getType() == Device.Type.AC) {
            binding.layoutAcControls.setVisibility(View.VISIBLE);
            viewModel.getAcSettings(device.getId()).observe(getViewLifecycleOwner(), settings -> {
                if (settings != null) {
                    binding.seekbarTemp.setProgress(settings.getTemperature());
                    binding.textTempValue.setText(settings.getTemperature() + "°C");
                }
            });
        } else {
            binding.layoutAcControls.setVisibility(View.GONE);
        }
    }

    private void switchEnabledInDetails(boolean isEnabled) {
        binding.switchDetailEnabled.setChecked(isEnabled);
        binding.switchDetailEnabled.setText(isEnabled ? R.string.label_on : R.string.label_off);
        binding.switchDetailEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Device updated = device.copy();
            updated.setEnabled(isChecked);
            viewModel.updateDevice(updated);
        });
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
