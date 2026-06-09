package com.example.mycoursework.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.ActivityMainBinding;
import com.example.mycoursework.viewmodel.DeviceViewModel;
import com.example.mycoursework.viewmodel.LoginViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LoginViewModel loginViewModel;
    private DeviceViewModel deviceViewModel;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Define top-level destinations including User List for Admin
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_login, R.id.navigation_home, R.id.navigation_settings, R.id.navigation_user_list)
                    .build();

            NavigationUI.setupWithNavController(binding.bottomNav, navController);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Hide bottom navigation on auth screens
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.navigation_login || destination.getId() == R.id.navigation_register) {
                    binding.bottomNav.setVisibility(View.GONE);
                } else {
                    if (loginViewModel.getCurrentUser().getValue() != null) {
                        binding.bottomNav.setVisibility(View.VISIBLE);
                    }
                }
            });

            // Handle role-based UI and immediate navigation
            loginViewModel.getCurrentUser().observe(this, user -> {
                if (user != null) {
                    deviceViewModel.setCurrentUser(user.getUsername());
                    
                    int currentId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1;
                    if (currentId != R.id.navigation_login && currentId != R.id.navigation_register) {
                        binding.bottomNav.setVisibility(View.VISIBLE);
                    }
                    
                    if (user.isAdmin()) {
                        // Admin: Show User List tab, Hide Home tab
                        binding.bottomNav.getMenu().findItem(R.id.navigation_home).setVisible(false);
                        binding.bottomNav.getMenu().findItem(R.id.navigation_user_list).setVisible(true);

                        // If admin is on Home (default start), immediately jump to User List
                        if (currentId == R.id.navigation_home || currentId == R.id.navigation_login) {
                            navController.navigate(R.id.navigation_user_list, null, 
                                new NavOptions.Builder()
                                    .setPopUpTo(R.id.navigation_home, true)
                                    .build());
                        }
                    } else {
                        // Regular User: Show Home tab, Hide User List tab
                        binding.bottomNav.getMenu().findItem(R.id.navigation_home).setVisible(true);
                        binding.bottomNav.getMenu().findItem(R.id.navigation_user_list).setVisible(false);
                    }
                } else {
                    binding.bottomNav.setVisibility(View.GONE);
                }
            });
        }

        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_login, R.id.navigation_home, R.id.navigation_settings, R.id.navigation_user_list)
                    .build();
            return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
