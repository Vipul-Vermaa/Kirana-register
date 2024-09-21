package com.example.kirana.Service;

import com.example.kirana.Model.UserModel;
import com.example.kirana.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.kirana.Utils.UserRegistrationDTO;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Method to register a new user
    public UserModel registerUser(UserRegistrationDTO userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already in use");
        }
        UserModel user = new UserModel();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "ROLE_READ_ONLY");// Encrypting the password
        return userRepository.save(user);
    }

    public UserModel loginUser(String email, String password) {
        UserModel user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }
}
