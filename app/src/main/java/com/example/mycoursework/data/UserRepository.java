package com.example.mycoursework.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mycoursework.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class UserRepository {
    private static UserRepository instance;
    private final UserDao userDao;
    private final ExecutorService executor;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    private UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public static synchronized UserRepository getInstance(Application application) {
        if (instance == null) {
            instance = new UserRepository(application);
        }
        return instance;
    }

    public interface AuthCallback {
        void onResult(boolean success, String message);
    }

    public void register(String username, String password, User.Role role, AuthCallback callback) {
        executor.execute(() -> {
            User existing = userDao.getUserByUsername(username);
            if (existing != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(false, "Username already exists"));
                return;
            }
            userDao.insert(new User(username, password, role));
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(true, "Registration successful"));
        });
    }

    public void login(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    currentUser.setValue(user);
                    callback.onResult(true, "Login successful");
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(false, "Invalid username or password"));
            }
        });
    }

    public void deleteUser(String username) {
        executor.execute(() -> userDao.deleteByUsername(username));
    }

    public void logout() {
        currentUser.setValue(null);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }
}
