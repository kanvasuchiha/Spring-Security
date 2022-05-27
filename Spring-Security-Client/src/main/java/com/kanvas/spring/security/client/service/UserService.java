package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.entity.VerificationToken;
import com.kanvas.spring.security.client.model.UserModel;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String applicationUrl(HttpServletRequest request);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void resendVerificationToken(User user, String applicationUrl, VerificationToken verificationToken);
}
