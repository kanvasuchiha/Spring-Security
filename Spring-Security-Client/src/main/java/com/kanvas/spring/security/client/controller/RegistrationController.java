package com.kanvas.spring.security.client.controller;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.entity.VerificationToken;
import com.kanvas.spring.security.client.event.RegistrationCompleteEvent;
import com.kanvas.spring.security.client.model.UserModel;
import com.kanvas.spring.security.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request){
        User user = userService.registerUser(userModel);

        // This event will handle creating and sending the email,
        // since we don't want to wait for the entirety of this process
        ApplicationEvent event = new RegistrationCompleteEvent(user, userService.applicationUrl(request));
        publisher.publishEvent(event);
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        return result.equalsIgnoreCase("valid") ? "User verified successfully" : "Bad user";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        userService.resendVerificationToken(user, userService.applicationUrl(request), verificationToken);
        return "Re-verification link sent";
    }

}

