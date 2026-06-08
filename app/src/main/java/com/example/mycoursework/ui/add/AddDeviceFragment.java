package com.example.mycoursework.ui.add;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentAddDeviceBinding;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.viewmodel.DeviceViewModel;

public class AddDeviceFragment extends Fragment {

    private FragmentAddDeviceBinding binding;
    private DeviceViewModel viewModel;
    private DeviceImageAdapter imageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddDeviceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

        // Setup ViewPager for device types
        imageAdapter = new DeviceImageAdapter();
        binding.viewPagerDeviceImages.setAdapter(imageAdapter);

        // Setup AutoCompleteTextView for rooms
        viewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, rooms);
            binding.editDeviceRoom.setAdapter(adapter);
        });

        binding.buttonSaveDevice.setOnClickListener(v -> {
            String name = binding.editDeviceName.getText().toString().trim();
            String room = binding.editDeviceRoom.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                binding.layoutDeviceName.setError(getString(R.string.error_empty_name));
                return;
            }

            int currentItem = binding.viewPagerDeviceImages.getCurrentItem();
            Device.Type type = imageAdapter.getTypeAt(currentItem);
            int imageResId = imageAdapter.getImageResAt(currentItem);

            Device newDevice = new Device(name, TextUtils.isEmpty(room) ? "Unknown" : room, type, imageResId);
            viewModel.addDevice(newDevice);

            Toast.makeText(requireContext(), R.string.device_added_success, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
