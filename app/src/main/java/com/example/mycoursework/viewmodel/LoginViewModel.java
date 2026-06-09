package com.example.mycoursework.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mycoursework.data.UserRepository;
import com.example.mycoursework.model.User;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> loginError = new MutableLiveData<>();
    private final MutableLiveData<String> registerError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = UserRepository.getInstance(application);
    }

    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<String> getRegisterError() {
        return registerError;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            loginError.setValue("Please fill all fields");
            return;
        }
        userRepository.login(username, password, (success, message) -> {
            if (success) {
                loginSuccess.setValue(true);
            } else {
                loginError.setValue(message);
            }
        });
    }

    public void register(String username, String password, User.Role role) {
        if (username.isEmpty() || password.isEmpty()) {
            registerError.setValue("Please fill all fields");
            return;
        }
        userRepository.register(username, password, role, (success, message) -> {
            if (success) {
                registerSuccess.setValue(true);
            } else {
                registerError.setValue(message);
            }
        });
    }

    public void resetAuthStatus() {
        registerSuccess.setValue(null);
        registerError.setValue(null);
        loginSuccess.setValue(null);
        loginError.setValue(null);
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
    }

    public void logout() {
        userRepository.logout();
        loginSuccess.setValue(false);
    }
}
