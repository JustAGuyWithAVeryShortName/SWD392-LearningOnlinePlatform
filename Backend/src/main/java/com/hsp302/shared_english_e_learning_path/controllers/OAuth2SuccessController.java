package com.hsp302.shared_english_e_learning_path.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2SuccessController {

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> handleOAuth2Success(@RequestParam("token") String token) {
        return ResponseEntity.ok("Login successful. Token: " + token);
    }
}
