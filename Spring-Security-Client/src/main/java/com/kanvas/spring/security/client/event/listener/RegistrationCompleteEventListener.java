package com.kanvas.spring.security.client.event.listener;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.event.RegistrationCompleteEvent;
import com.kanvas.spring.security.client.service.EmailSenderService;
import com.kanvas.spring.security.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create the Verification Token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        //Send mail to user
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        //sendVerificationEmail()
        emailSenderService.sendSimpleEmail(user.getEmail(), "Activate your account", "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " + url);
        log.info("Click the link to verify your account: {}", url);

    }

}
