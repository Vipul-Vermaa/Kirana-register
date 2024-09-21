package com.example.kirana.Controller;


import com.example.kirana.Model.UserModel;
import com.example.kirana.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.kirana.Utils.UserRegistrationDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(
            @RequestBody UserRegistrationDTO userDto) {
        try {
            UserModel user = userService.registerUser(userDto);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserModel> loginUser(
            @RequestParam String email,
            @RequestParam String password) {
        try {
            UserModel user = userService.loginUser(email, password);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
