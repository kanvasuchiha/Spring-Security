package com.kanvas.spring.security.client.controller;

import com.kanvas.spring.security.client.entity.PasswordResetToken;
import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.entity.VerificationToken;
import com.kanvas.spring.security.client.event.RegistrationCompleteEvent;
import com.kanvas.spring.security.client.model.PasswordModel;
import com.kanvas.spring.security.client.model.UserModel;
import com.kanvas.spring.security.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

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
        userService.resendVerificationTokenMail(user, userService.applicationUrl(request), verificationToken);
        return "Re-verification link sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, final HttpServletRequest request){
        User user =  userService.findUserByEmail(passwordModel.getEmail());
        if(user!=null){
            PasswordResetToken passwordResetToken =  userService.createPasswordResetTokenForUser(user);
            userService.passwordResetTokenMail(user, userService.applicationUrl(request), passwordResetToken);
            return "Reset password mail sent.";
        }
        return "Bad request";
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            //We can create the mechanism to double-check the old password in the DB here
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Reset Successful";
        }else{
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if(user == null || !userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())){
            return "Invalid old password";
        }
        //Save new Password functionality to return
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully";
    }

}

