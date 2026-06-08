package com.example.mycoursework.ui.add;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoursework.R;
import com.example.mycoursework.model.Device;

import java.util.Arrays;
import java.util.List;

public class DeviceImageAdapter extends RecyclerView.Adapter<DeviceImageAdapter.ImageViewHolder> {

    private final List<Device.Type> types = Arrays.asList(Device.Type.LIGHT, Device.Type.AC, Device.Type.SOCKET);
    private final List<Integer> images = Arrays.asList(R.drawable.img_lamp, R.drawable.img_ac, R.drawable.img_plug);

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public Device.Type getTypeAt(int position) {
        return types.get(position);
    }

    public int getImageResAt(int position) {
        return images.get(position);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_device_type);
        }
    }
}
