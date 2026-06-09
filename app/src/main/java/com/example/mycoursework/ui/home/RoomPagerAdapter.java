package com.example.mycoursework.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mycoursework.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomPagerAdapter extends FragmentStateAdapter {

    private final List<Room> rooms = new ArrayList<>();

    public RoomPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void setRooms(List<Room> newRooms) {
        rooms.clear();
        if (newRooms != null) {
            rooms.addAll(newRooms);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Pass the unique room ID to the fragment
        return RoomFragment.newInstance(rooms.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public String getRoomName(int position) {
        return rooms.get(position).getName();
    }
}
