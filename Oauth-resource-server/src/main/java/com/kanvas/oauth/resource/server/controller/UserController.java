package com.kanvas.oauth.resource.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users")
    public String[] user(){
        return new String[]{
                "Shabbir",
                "Nikhil",
                "Shivam"
        };
    }

}
