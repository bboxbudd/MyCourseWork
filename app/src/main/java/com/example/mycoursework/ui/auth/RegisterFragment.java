package com.example.mycoursework.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mycoursework.R;
import com.example.mycoursework.databinding.FragmentRegisterBinding;
import com.example.mycoursework.model.User;
import com.example.mycoursework.viewmodel.LoginViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // Сбрасываем статус при входе на экран, чтобы старые уведомления не срабатывали
        viewModel.resetAuthStatus();

        binding.registerButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            User.Role role = binding.radioAdmin.isChecked() ? User.Role.ADMIN : User.Role.USER;

            viewModel.register(username, password, role);
        });

        binding.goToLoginButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_register_to_login);
        });

        viewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                viewModel.resetAuthStatus(); // Сбрасываем после успешного выполнения
                Navigation.findNavController(requireView()).navigate(R.id.action_register_to_login);
            }
        });

        viewModel.getRegisterError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.resetAuthStatus(); // Сбрасываем ошибку после показа
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
