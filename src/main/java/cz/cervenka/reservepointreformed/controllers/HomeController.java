package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.CustomerEntity;
import cz.cervenka.reservepointreformed.database.entities.ReservationEntity;
import cz.cervenka.reservepointreformed.database.repositories.ServiceRepository;
import cz.cervenka.reservepointreformed.database.repositories.UserRepository;
import cz.cervenka.reservepointreformed.services.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public HomeController(ReservationService reservationService, UserRepository userRepository, ServiceRepository serviceRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/loginForm";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }

    @GetMapping("/reserveForm")
    public String showReservationForm(Model model, Authentication authentication) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);

        if (customerOpt.isEmpty()) {
            CustomerEntity newCustomer = new CustomerEntity();
            newCustomer.setUser(userRepository.findByEmail(authentication.getName()).orElseThrow());
            model.addAttribute("customer", newCustomer);
        } else {
            model.addAttribute("customer", customerOpt.get());
        }

        model.addAttribute("reservation", new ReservationEntity());
        model.addAttribute("services", serviceRepository.findAll());
        return "reserveForm";
    }

    @PostMapping("/make-reservation")
    public String handleReservationSubmission(
            @ModelAttribute("customer") CustomerEntity customer,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication,
            Model model) {

        if (phone == null || phone.isBlank() ||
                address == null || address.isBlank() ||
                serviceId == null) {

            model.addAttribute("errorMessage", "All required fields must be filled out.");
            model.addAttribute("services", serviceRepository.findAll());
            model.addAttribute("customer", customer);
            return "reserveForm";
        }

        CustomerEntity finalCustomer = reservationService.saveCustomer(authentication, customer);
        ReservationEntity reservation = reservationService.createReservation(finalCustomer, serviceId, notes);

        reservation.setFormattedCreatedAt(
                reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
        );

        model.addAttribute("customer", finalCustomer);
        model.addAttribute("reservation", reservation);
        model.addAttribute("formattedCreatedAt", reservation.getFormattedCreatedAt());

        return "confirmation";
    }
}