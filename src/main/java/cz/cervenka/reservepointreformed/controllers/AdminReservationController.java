package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.ReservationEntity;
import cz.cervenka.reservepointreformed.database.repositories.ReservationRepository;
import cz.cervenka.reservepointreformed.database.repositories.ServiceRepository;
import cz.cervenka.reservepointreformed.services.AdminReservationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
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
    public String getAdminReservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String serviceType,
            Model model) {

        List<ReservationEntity> reservations = reservationService.getFilteredReservations(name, surname, date, serviceType);
        model.addAttribute("reservations", reservations);
        model.addAttribute("services", serviceRepository.findAll());
        return "admin/reservations";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/admin/reservations";
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

        return "admin/reservation-detail";
    }

    @PostMapping("/update")
    public String updateReservation(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String orderedTime,
            @RequestParam(required = false) String notes) {

        ReservationEntity updatedReservation = reservationService.updateReservation(id, status, orderedTime, notes);
        if (updatedReservation == null) {
            return "redirect:/admin/reservations";
        }

        return "redirect:/admin/reservations/" + id;
    }

    @PostMapping("/approve")
    public String approveReservation(
            @RequestParam Long id,
            @RequestParam(required = false) String orderedTime,
            RedirectAttributes redirectAttributes) {

        ReservationEntity updatedReservation = reservationService.approveReservation(id, orderedTime);

        if (updatedReservation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected time is already taken by another reservation.");
            return "redirect:/admin/reservations/" + id;
        }

        return "redirect:/admin/reservations/" + id;
    }


    @PostMapping("/reject")
    public String rejectReservation(@RequestParam Long id, @RequestParam(required = false) String notes) {
        reservationService.rejectReservation(id, notes);
        return "redirect:/admin/reservations";
    }

    @PostMapping("/delete")
    public String deleteReservation(@RequestParam Long id, @RequestParam(required = false) String notes) {
        reservationService.deleteReservation(id, notes);
        return "redirect:/admin/reservations";
    }

    @PostMapping("/calendar")
    public String showCalendar() {
        return "redirect:/admin/calendar";
    }
}