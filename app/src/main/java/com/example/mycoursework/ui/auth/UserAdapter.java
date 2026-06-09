package com.example.mycoursework.ui.auth;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoursework.databinding.ItemUserBinding;
import com.example.mycoursework.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();
    private OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onUserDelete(User user);
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void setOnUserDeleteListener(OnUserDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position), deleteListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        public UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user, OnUserDeleteListener listener) {
            binding.textUserName.setText("Имя пользователя: " + user.getUsername());
            binding.textUserPassword.setText("Пароль: " + user.getPassword());
            binding.textUserRole.setText("Роль: " + user.getRole().name());

            // Admin cannot delete themselves in this simple implementation
            binding.buttonDeleteUser.setVisibility(user.isAdmin() ? android.view.View.GONE : android.view.View.VISIBLE);

            binding.buttonDeleteUser.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserDelete(user);
                }
            });
        }
    }
}
