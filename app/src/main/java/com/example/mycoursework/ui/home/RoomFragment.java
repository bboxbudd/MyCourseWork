package com.example.mycoursework.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentRoomBinding;
import com.example.mycoursework.model.Device;
import com.example.mycoursework.viewmodel.DeviceViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class RoomFragment extends Fragment implements DeviceAdapter.OnDeviceClickListener {

    private static final String ARG_ROOM_ID = "room_id";
    private FragmentRoomBinding binding;
    private DeviceViewModel viewModel;
    private DeviceAdapter adapter;
    private String roomId;

    public static RoomFragment newInstance(String roomId) {
        RoomFragment fragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_ID, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getString(ARG_ROOM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        adapter = new DeviceAdapter(this);
        binding.recyclerDevices.setAdapter(adapter);

        viewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
            if (devices != null) {
                List<Device> filteredDevices = devices.stream()
                        .filter(d -> d.getRoomId().equals(roomId))
                        .collect(Collectors.toList());
                adapter.submitList(filteredDevices);
            }
        });
    }

    @Override
    public void onDeviceClick(Device device) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        Navigation.findNavController(requireView()).navigate(R.id.action_home_to_details, bundle);
    }

    @Override
    public void onDeviceToggled(Device device, boolean isEnabled) {
        Device updated = device.copy();
        updated.setEnabled(isEnabled);
        viewModel.updateDevice(updated);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
