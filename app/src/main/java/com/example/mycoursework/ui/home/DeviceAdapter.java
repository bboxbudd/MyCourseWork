package com.example.mycoursework.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoursework.databinding.ItemDeviceBinding;
import com.example.mycoursework.model.Device;

public class DeviceAdapter extends ListAdapter<Device, DeviceAdapter.DeviceViewHolder> {

    private final OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(Device device);
        void onDeviceToggled(Device device, boolean isEnabled);
    }

    public DeviceAdapter(OnDeviceClickListener listener) {
        super(new DeviceDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DeviceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final ItemDeviceBinding binding;

        public DeviceViewHolder(ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Device device, OnDeviceClickListener listener) {
            binding.textDeviceName.setText(device.getName());
            
            // Отображение температуры только для кондиционера
            if (device.getType() == Device.Type.AC && device.getTemperature() != null) {
                binding.textDeviceExtra.setVisibility(View.VISIBLE);
                binding.textDeviceExtra.setText(device.getTemperature() + "°C");
            } else {
                binding.textDeviceExtra.setVisibility(View.GONE);
            }
            
            // Таймер полностью удален из логики

            binding.imageDeviceType.setImageResource(device.getImageResId());
            binding.switchEnabled.setOnCheckedChangeListener(null);
            binding.switchEnabled.setChecked(device.isEnabled());

            // Interaction
            binding.getRoot().setOnClickListener(v -> listener.onDeviceClick(device));
            binding.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onDeviceToggled(device, isChecked);
            });
        }
    }

    static class DeviceDiffCallback extends DiffUtil.ItemCallback<Device> {
        @Override
        public boolean areItemsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
            return oldItem.equals(newItem);
        }
    }
}
