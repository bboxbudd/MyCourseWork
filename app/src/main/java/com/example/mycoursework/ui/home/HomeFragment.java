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
import androidx.viewpager2.widget.ViewPager2;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentHomeBinding;
import com.example.mycoursework.viewmodel.DeviceViewModel;
import com.example.mycoursework.viewmodel.LoginViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DeviceViewModel viewModel;
    private LoginViewModel loginViewModel;
    private RoomPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        
        pagerAdapter = new RoomPagerAdapter(this);
        binding.viewPagerRooms.setAdapter(pagerAdapter);

        // Role-based UI: Users can add devices, Admin doesn't even see this screen
        loginViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && !user.isAdmin()) {
                binding.fabAddDevice.setVisibility(View.VISIBLE);
                viewModel.setCurrentUser(user.getUsername());
            } else {
                binding.fabAddDevice.setVisibility(View.GONE);
            }
        });

        viewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            pagerAdapter.setRooms(rooms);
            new TabLayoutMediator(binding.tabsRooms, binding.viewPagerRooms,
                    (tab, position) -> tab.setText(pagerAdapter.getRoomName(position))
            ).attach();

            // Restore last selected room
            String lastRoom = viewModel.getSelectedRoom().getValue();
            if (lastRoom != null && rooms != null) {
                for (int i = 0; i < rooms.size(); i++) {
                    if (rooms.get(i).getName().equals(lastRoom)) {
                        binding.viewPagerRooms.setCurrentItem(i, false);
                        break;
                    }
                }
            }
        });

        binding.viewPagerRooms.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (pagerAdapter.getItemCount() > position) {
                    viewModel.setSelectedRoom(pagerAdapter.getRoomName(position));
                }
            }
        });

        binding.fabAddDevice.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_add);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
