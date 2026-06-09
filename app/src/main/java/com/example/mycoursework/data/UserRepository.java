package com.example.mycoursework.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mycoursework.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static UserRepository instance;
    private final List<User> users = new ArrayList<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    private UserRepository() {
        // Default admin user
        users.add(new User("admin", "admin", User.Role.ADMIN));
        users.add(new User("user", "user", User.Role.USER));
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public boolean register(String username, String password, User.Role role) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return false;
            }
        }
        users.add(new User(username, password, role));
        return true;
    }

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                currentUser.setValue(u);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser.setValue(null);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
}
