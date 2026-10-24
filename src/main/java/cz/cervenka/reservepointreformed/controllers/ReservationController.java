package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.*;
import cz.cervenka.reservepointreformed.database.repositories.ReservationRepository;
import cz.cervenka.reservepointreformed.database.repositories.ServiceRepository;
import cz.cervenka.reservepointreformed.services.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
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
    public String getUserReservations(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String surname,
                                      @RequestParam(required = false) String date,
                                      @RequestParam(required = false) String serviceType,
                                      Authentication authentication, Model model) {
        Optional<CustomerEntity> customerOpt = reservationService.getAuthenticatedCustomer(authentication);
        if (customerOpt.isEmpty()) {
            return "redirect:/home";
        }

        List<ReservationEntity> reservations = reservationService.getFilteredUserReservations(customerOpt.get(), name, surname, date, serviceType);

        model.addAttribute("reservations", reservations);
        model.addAttribute("customer", customerOpt.get());
        model.addAttribute("services", serviceRepository.findAll());
        return "reservations";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/reservations";
        }

        ReservationEntity reservation = reservationOpt.get();

        reservation.setFormattedCreatedAt(
                reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
        );
        reservation.setFormattedOrderTime(
                reservation.getOrderedTime() != null ? reservation.getOrderedTime().format(formatter) : "N/A"
        );

        model.addAttribute("reservation", reservation);
        model.addAttribute("formattedCreatedAt", reservation.getFormattedCreatedAt());
        model.addAttribute("formattedOrderTime", reservation.getFormattedOrderTime());
        model.addAttribute("services", serviceRepository.findAll());
        return "reservation-detail";
    }

    @PostMapping("/{id}/request-confirmation")
    public String requestConfirmation(@PathVariable Long id) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/reservations";
        }

        reservationService.requestReservationConfirmation(id);

        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/request-modification")
    public String requestModification(@PathVariable Long id,
                                      @RequestParam(required = false) String newNotes,
                                      @RequestParam(required = false) ServiceEntity newService,
                                      @RequestParam(required = false) String newOrderTime,
                                      Model model) {

        try {
            reservationService.requestReservationModification(id, newNotes, newService, newOrderTime);
            return "redirect:/reservations/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorModificationMessage", e.getMessage());
            return "reservation-detail";
        }
    }

    @PostMapping("/{id}/request-cancellation")
    public String requestCancellation(@PathVariable Long id,
                                      @RequestParam String reason,
                                      Model model) {
        if (reason == null || reason.isBlank()) {
            model.addAttribute("errorCancellationMessage", "Reason cannot be empty.");
            return "reservation-detail";
        }

        reservationService.requestReservationCancellation(id, reason);
        return "redirect:/reservations/" + id;
    }
}