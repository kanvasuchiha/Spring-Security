package com.kanvas.spring.security.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private String firstName;
    private String lastName;
    private String email;
    //Need to do the validation to check if password and matchingPassword are same or not, before saving in repository
    private String password;
    private String matchingPassword;

}
