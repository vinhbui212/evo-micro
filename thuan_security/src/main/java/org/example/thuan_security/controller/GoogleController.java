package org.example.thuan_security.controller;

import org.example.thuan_security.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://127.0.0.1:5500/*")
public class GoogleController {
    @Autowired
    private UserService userService;
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");

            String token=principal.getAttribute("token");
            return "Hello, " + name + " (" + email + ")" ;
        } else {
            return "Not authenticated";
        }
    }
}
