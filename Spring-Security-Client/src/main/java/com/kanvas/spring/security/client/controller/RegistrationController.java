package com.kanvas.spring.security.client.controller;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.model.UserModel;
import com.kanvas.spring.security.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel){
        User user = userService.registerUser(userModel);
        return "Success";
    }

}
