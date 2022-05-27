package com.kanvas.spring.security.client.service;

import com.kanvas.spring.security.client.entity.User;
import com.kanvas.spring.security.client.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);
}
