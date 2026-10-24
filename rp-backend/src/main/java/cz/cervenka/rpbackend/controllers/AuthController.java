package cz.cervenka.rpbackend.controllers;

import cz.cervenka.rpbackend.database.entities.UserEntity;
import cz.cervenka.rpbackend.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registerForm")
    public ResponseEntity<Map<String, Object>> showRegistrationForm(Model model) {
        Map<String, Object> responseData = Map.of("user", new UserEntity());
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@ModelAttribute UserEntity user,
                                                           @RequestParam String name,
                                                           @RequestParam String surname,
                                                           @RequestParam String password,
                                                           @RequestParam String email) {
        try {
            if (name == null || name.isBlank()
                    || surname == null || surname.isBlank()
                    || password == null || password.isBlank()
                    || email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("errorMessage", "Fields cannot be left empty."));
            }

            authService.registerUser(user);
            return ResponseEntity.ok().body(Map.of("message", "User registered successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "Email is already registered."));
        }
    }

    @GetMapping("/loginForm")
    public ResponseEntity<Map<String, Object>> showLoginForm() {
        return ResponseEntity.ok().build();
    }
}