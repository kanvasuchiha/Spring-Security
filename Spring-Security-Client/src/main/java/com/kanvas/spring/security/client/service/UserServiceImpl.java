package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.model.UserModel;
import com.kanvas.spring.security.client.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        // Password encrypted via BCrypt PasswordEncoder
        // There are a few encoding mechanisms supported by Spring Security
        // and for the article, we'll use BCrypt, as it's usually the best solution available.
        User user = User.builder()
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .role("USER")
                .password(passwordEncoder.encode(userModel.getPassword()))
                .build();

        userRepository.save(user);
        return null;
    }
}
