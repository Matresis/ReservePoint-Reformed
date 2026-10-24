package cz.cervenka.rpbackend.controllers;

import cz.cervenka.rpbackend.database.entities.CustomerEntity;
import cz.cervenka.rpbackend.database.entities.ReservationEntity;
import cz.cervenka.rpbackend.database.repositories.ServiceRepository;
import cz.cervenka.rpbackend.database.repositories.UserRepository;
import cz.cervenka.rpbackend.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
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
    public ResponseEntity<Void> index() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    public ResponseEntity<Void> showHome() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/make-reservation")
    public ResponseEntity<Map<String, Object>> handleReservationSubmission(
            @ModelAttribute("customer") CustomerEntity customer,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication) {

        if (phone == null || phone.isBlank() ||
                address == null || address.isBlank() ||
                serviceId == null) {

            Map<String, Object> responseData = Map.of(
                "errorMessage", "All required fields must be filled out.",
                "services", serviceRepository.findAll(),
                "customer", customer
            );
            return ResponseEntity.badRequest().body(responseData);
        }

        CustomerEntity finalCustomer = reservationService.saveCustomer(authentication, customer);
        ReservationEntity reservation = reservationService.createReservation(finalCustomer, serviceId, notes);

        reservation.setFormattedCreatedAt(
                reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
        );

        Map<String, Object> responseData = Map.of(
                "customer", finalCustomer,
                "reservation", reservation,
                "formattedCreatedAt", reservation.getFormattedCreatedAt()
        );

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/reserveForm")
    public ResponseEntity<Map<String, Object>> showReservationForm(Authentication authentication) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);

        Map<String, Object> responseData = new HashMap<>();

        if (customerOpt.isEmpty()) {
            CustomerEntity newCustomer = new CustomerEntity();
            newCustomer.setUser(userRepository.findByEmail(authentication.getName()).orElseThrow());
            responseData.put("customer", newCustomer);
        } else {
            responseData.put("customer", customerOpt.get());
        }

        responseData.put("reservation", new ReservationEntity());
        responseData.put("services", serviceRepository.findAll());
        return ResponseEntity.ok(responseData);
    }
}