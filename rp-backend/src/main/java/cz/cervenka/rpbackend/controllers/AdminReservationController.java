package cz.cervenka.rpbackend.controllers;

import cz.cervenka.rpbackend.database.entities.ReservationEntity;
import cz.cervenka.rpbackend.database.repositories.ReservationRepository;
import cz.cervenka.rpbackend.database.repositories.ServiceRepository;
import cz.cervenka.rpbackend.services.AdminReservationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AdminReservationController(AdminReservationService reservationService, ReservationRepository reservationRepository, ServiceRepository serviceRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAdminReservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType) {

        List<ReservationEntity> reservations = reservationService.getFilteredReservations(name, surname, date, serviceType);
        return ResponseEntity.ok().body(Map.of("reservations", reservations, "services", serviceRepository.findAll()));
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
                "formattedOrderTime", reservation.getFormattedOrderTime()
        );

        return ResponseEntity.ok().body(responseData);
    }

    @PostMapping("/update")
    public ResponseEntity<ReservationEntity> updateReservation(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String orderedTime,
            @RequestParam(required = false) String notes) {

        ReservationEntity updatedReservation = reservationService.updateReservation(id, status, orderedTime, notes);
        if (updatedReservation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(updatedReservation);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveReservation(
            @RequestParam Long id,
            @RequestParam(required = false) String orderedTime,
            @RequestParam(required = false) String notes) {

        ReservationEntity updatedReservation = reservationService.approveReservation(id, orderedTime);

        if (updatedReservation == null) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "Selected time is already taken by another reservation."));
        }

        return ResponseEntity.ok().body(updatedReservation);
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectReservation(@RequestParam Long id, @RequestParam(required = false) String notes) {
        reservationService.rejectReservation(id, notes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteReservation(@RequestParam Long id, @RequestParam(required = false) String notes) {
        reservationService.deleteReservation(id, notes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calendar")
    public ResponseEntity<Void> showCalendar() {
        return ResponseEntity.ok().build();
    }
}