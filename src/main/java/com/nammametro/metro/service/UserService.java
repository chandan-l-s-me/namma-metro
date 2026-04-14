package com.nammametro.metro.service;

import com.nammametro.metro.model.User;
import com.nammametro.metro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Register user
    public User register(User user) {

        User savedUser = userRepository.save(user);

        notificationService.notifyUsers("New user registered: " + user.getName());

        return savedUser;
    }

    // Login user
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return "Login Successful";
        } else {
            return "Invalid Credentials";
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}