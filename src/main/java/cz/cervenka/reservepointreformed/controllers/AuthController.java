package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.UserEntity;
import cz.cervenka.reservepointreformed.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "auth/registerForm";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user,
                               @RequestParam String name,
                               @RequestParam String surname,
                               @RequestParam String password,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        try {
            if (name == null || name.isBlank()
                    || surname == null || surname.isBlank()
                    || password == null || password.isBlank()
                    || email == null || email.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fields cannot be left empty.");
            }

            authService.registerUser(user);
            return "redirect:/loginForm";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is already registered.");
            return "redirect:/registerForm";
        }
    }

    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "auth/loginForm";
    }
}