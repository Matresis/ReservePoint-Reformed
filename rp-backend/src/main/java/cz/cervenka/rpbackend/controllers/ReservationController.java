package cz.cervenka.rpbackend.controllers;

import cz.cervenka.rpbackend.database.entities.*;
import cz.cervenka.rpbackend.database.repositories.ReservationRepository;
import cz.cervenka.rpbackend.database.repositories.ServiceRepository;
import cz.cervenka.rpbackend.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, ServiceRepository serviceRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getUserReservations(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String surname,
                                                                   @RequestParam(required = false) String date,
                                                                   @RequestParam(required = false) String serviceType,
                                                                   Authentication authentication) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ReservationEntity> reservations = reservationService.getFilteredUserReservations(customerOpt.get(), name, surname, date, serviceType);

        Map<String, Object> responseData = Map.of(
                "reservations", reservations,
                "customer", customerOpt.get(),
                "services", serviceRepository.findAll()
        );

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> viewReservation(@PathVariable Long id) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ReservationEntity reservation = reservationOpt.get();

        reservation.setFormattedCreatedAt(
                reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
        );
        reservation.setFormattedOrderTime(
                reservation.getOrderedTime() != null ? reservation.getOrderedTime().format(formatter) : "N/A"
        );

        Map<String, Object> responseData = Map.of(
                "reservation", reservation,
                "formattedCreatedAt", reservation.getFormattedCreatedAt(),
                "formattedOrderTime", reservation.getFormattedOrderTime(),
                "services", serviceRepository.findAll()
        );

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/{id}/request-confirmation")
    public ResponseEntity<String> requestConfirmation(@PathVariable Long id) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        reservationService.requestReservationConfirmation(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/request-modification")
    public ResponseEntity<Map<String, Object>> requestModification(@PathVariable Long id,
                                                     @RequestParam(required = false) String newNotes,
                                                     @RequestParam(required = false) ServiceEntity newService,
                                                     @RequestParam(required = false) String newOrderTime) {

        try {
            reservationService.requestReservationModification(id, newNotes, newService, newOrderTime);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("errorModificationMessage", e.getMessage()));
        }
    }

    @PostMapping("/{id}/request-cancellation")
    public ResponseEntity<Map<String, Object>> requestCancellation(@PathVariable Long id,
                                                      @RequestParam String reason) {
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("errorCancellationMessage", "Reason cannot be empty."));
        }

        reservationService.requestReservationCancellation(id, reason);
        return ResponseEntity.ok().build();
    }
}