package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.exception.MailNotSentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("spring.securoty.tutorial@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try{
            mailSender.send(message);
            System.out.println("Mail sent successfully to " + toEmail);
        } catch (MailException e){
            throw new MailNotSentException("Exception occurred when sending mail to " + toEmail);
        }
    }

}
