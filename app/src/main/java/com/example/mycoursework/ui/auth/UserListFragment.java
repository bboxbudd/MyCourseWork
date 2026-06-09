package com.example.mycoursework.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mycoursework.databinding.FragmentUserListBinding;
import com.example.mycoursework.viewmodel.LoginViewModel;

public class UserListFragment extends Fragment {

    private FragmentUserListBinding binding;
    private LoginViewModel viewModel;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        adapter = new UserAdapter();
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsers.setAdapter(adapter);

        adapter.setOnUserDeleteListener(user -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить пользователя")
                    .setMessage("Вы уверены, что хотите удалить пользователы " + user.getUsername() + " и все их данные?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        viewModel.deleteUser(user.getUsername());
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        viewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                adapter.setUsers(users);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
