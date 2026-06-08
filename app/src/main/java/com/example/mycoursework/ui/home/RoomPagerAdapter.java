package com.example.mycoursework.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoomPagerAdapter extends FragmentStateAdapter {

    private final List<String> rooms = new ArrayList<>();

    public RoomPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void setRooms(List<String> newRooms) {
        rooms.clear();
        rooms.addAll(newRooms);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return RoomFragment.newInstance(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public String getRoomName(int position) {
        return rooms.get(position);
    }
}
