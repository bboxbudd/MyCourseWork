package com.example.mycoursework.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentSettingsBinding;
import com.example.mycoursework.viewmodel.LoginViewModel;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private LoginViewModel loginViewModel;
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // Update User Info
        loginViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textUsername.setText("Username: " + user.getUsername());
                binding.textRole.setText("Role: " + user.getRole().name());
            }
        });

        // Manage Users button is now redundant as it's in BottomNav, so we hide it
        binding.buttonManageUsers.setVisibility(View.GONE);

        // Dark Mode Logic
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        
        binding.switchDarkMode.setChecked(isDarkMode);
        
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Logout Logic - Clear BackStack
        binding.buttonLogout.setOnClickListener(v -> {
            loginViewModel.logout();
            Navigation.findNavController(v).navigate(R.id.navigation_login, null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, true)
                            .build());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
