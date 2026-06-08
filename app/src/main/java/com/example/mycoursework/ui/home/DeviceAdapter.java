package com.example.mycoursework.ui.home;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoursework.databinding.ItemDeviceBinding;
import com.example.mycoursework.model.Device;

import java.util.Locale;

public class DeviceAdapter extends ListAdapter<Device, DeviceAdapter.DeviceViewHolder> {

    private final OnDeviceClickListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
        holder.bind(getItem(position), listener, handler);
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final ItemDeviceBinding binding;
        private Runnable timerRunnable;

        public DeviceViewHolder(ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Device device, OnDeviceClickListener listener, Handler handler) {
            binding.textDeviceName.setText(device.getName());
            
            // Отображение температуры для кондиционера вместо комнаты
            if (device.getType() == Device.Type.AC) {
                binding.textDeviceExtra.setVisibility(View.VISIBLE);
                binding.textDeviceExtra.setText(device.getTemperature() + "°C");
            } else {
                binding.textDeviceExtra.setVisibility(View.GONE);
            }
            
            // Timer Logic
            if (timerRunnable != null) {
                handler.removeCallbacks(timerRunnable);
            }

            // Таймер не показываем для розеток (согласно предыдущему требованию)
            if (device.isTimerActive() && device.getType() != Device.Type.SOCKET) {
                binding.textTimer.setVisibility(View.VISIBLE);
                updateTimerText(device);
                
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateTimerText(device);
                        if (device.isTimerActive() && device.getType() != Device.Type.SOCKET) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.postDelayed(timerRunnable, 1000);
            } else {
                binding.textTimer.setVisibility(View.GONE);
            }

            binding.imageDeviceType.setImageResource(device.getImageResId());
            binding.switchEnabled.setOnCheckedChangeListener(null);
            binding.switchEnabled.setChecked(device.isEnabled());

            // Interaction
            binding.getRoot().setOnClickListener(v -> listener.onDeviceClick(device));
            binding.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Если кондиционер выключается, сбрасываем таймер
                if (device.getType() == Device.Type.AC && !isChecked) {
                    device.setTimerActive(false);
                }
                listener.onDeviceToggled(device, isChecked);
            });
        }

        private void updateTimerText(Device device) {
            long remainingMillis = (device.getTimerStartTime() + (long) device.getTimerMinutes() * 60 * 1000) - System.currentTimeMillis();
            if (remainingMillis <= 0) {
                binding.textTimer.setVisibility(View.GONE);
                device.setTimerActive(false);
            } else {
                long seconds = remainingMillis / 1000;
                long minutes = seconds / 60;
                long secs = seconds % 60;
                binding.textTimer.setText(String.format(Locale.getDefault(), "• %02d:%02d", minutes, secs));
            }
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
