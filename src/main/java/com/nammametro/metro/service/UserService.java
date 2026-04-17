package com.nammametro.metro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nammametro.metro.model.User;
import com.nammametro.metro.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // ✅ CREATE USER
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // ✅ GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ GET USER BY ID
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    // ✅ UPDATE USER
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            return userRepository.save(user);
        }

        return null;
    }

    // ✅ DELETE USER
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // -------------------------------
    // 🔽 LOGIN / REGISTER (Krupa part)
    // -------------------------------

    public User register(User user) {
        User savedUser = userRepository.save(user);
        notificationService.notifyUsers("New user registered: " + user.getName());
        return savedUser;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return "Login Successful";
        } else {
            return "Invalid Credentials";
        }
    }
}