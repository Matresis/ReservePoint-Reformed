package cz.cervenka.reservepointreformed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String getAdminHome() {
        return "admin/home";
    }

    @PostMapping("/logout")
    public String logoutAdmin() {
        return "redirect:/loginForm";
    }
}