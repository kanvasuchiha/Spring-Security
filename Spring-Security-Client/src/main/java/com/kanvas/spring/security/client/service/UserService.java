package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.entity.PasswordResetToken;
import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.entity.VerificationToken;
import com.kanvas.spring.security.client.model.UserModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String applicationUrl(HttpServletRequest request);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken);

    User findUserByEmail(String email);

    PasswordResetToken createPasswordResetTokenForUser(User user);

    void passwordResetTokenMail(User user, String applicationUrl, PasswordResetToken passwordResetToken);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
