package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.entity.PasswordResetToken;
import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.entity.VerificationToken;
import com.kanvas.spring.security.client.model.UserModel;
import com.kanvas.spring.security.client.repository.PasswordResetTokenRepository;
import com.kanvas.spring.security.client.repository.UserRepository;
import com.kanvas.spring.security.client.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    //Repositories autowired
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

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
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null){
            return "invalid";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "Token already expired";
        }

        user.setEnabled(true);
        userRepository.save(user);

        //Is there any point not deleting the verificationToken after setting isEnabled to True?????
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();
        //sendVerificationEmail()
        emailSenderService.sendSimpleEmail(user.getEmail(), "Activate your account", "Verification link received again. Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " + url);
        log.info("Click the link to verify your account: {}", url);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void passwordResetTokenMail(User user, String applicationUrl, PasswordResetToken passwordResetToken) {
        String url = applicationUrl + "/savePassword?token=" + passwordResetToken.getToken();
        //sendVerificationEmail()
        emailSenderService.sendSimpleEmail(user.getEmail(), "Activate your account", "Verification link received again. Thank you for signing up to Spring Reddit, " +
                "please click on the below url to Reset your password : " + url);
        log.info("Click the link to verify your account: {}", url);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken == null){
            return "invalid";
        }
        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime() <= 0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Token already expired";
        }
        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

}
