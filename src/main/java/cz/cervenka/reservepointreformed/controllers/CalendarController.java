package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.ReservationEntity;
import cz.cervenka.reservepointreformed.database.repositories.ReservationRepository;
import cz.cervenka.reservepointreformed.services.AdminReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/calendar")
public class CalendarController {

    private final ReservationRepository reservationRepository;
    private final AdminReservationService reservationService;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public CalendarController(ReservationRepository reservationRepository, AdminReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @GetMapping
    public String showCalendarPage(@RequestParam(name = "id", required = false) Long id, Model model) {
        model.addAttribute("highlightedId", id);
        return "admin/calendar";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getCalendarEvents() {
        return reservationRepository.findAll().stream()
                .filter(reservation ->
                        reservation.getOrderedTime() != null &&
                                "CONFIRMED".equalsIgnoreCase(String.valueOf(reservation.getStatus())))
                .map(reservation -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("title", reservation.getService().getName() + " - " + reservation.getCustomer().getUser().getName());
                    event.put("start", reservation.getOrderedTime().toString());
                    event.put("id", reservation.getId());
                    return event;
                }).collect(Collectors.toList());
    }

    @PostMapping("/add-event")
    public String addReservationToCalendar(@RequestParam("id") Long reservationId, Model model) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isPresent()) {
            ReservationEntity reservation = reservationOpt.get();

            if (reservation.getOrderedTime() == null) {
                model.addAttribute("errorMessage", "Order time must be entered before adding to the calendar.");
                model.addAttribute("reservation", reservation);
                return "admin/reservation-detail";
            }

            reservationRepository.save(reservation);
        }

        return "redirect:/admin/calendar";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getReservationDetails(@PathVariable Long id) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);

        if (reservationOpt.isPresent()) {
            ReservationEntity reservation = reservationOpt.get();

            reservation.setFormattedCreatedAt(
                    reservation.getCreatedAt() != null ? reservation.getCreatedAt().format(formatter) : "N/A"
            );
            reservation.setFormattedOrderTime(
                    reservation.getOrderedTime() != null ? reservation.getOrderedTime().format(formatter) : "N/A"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("customer", Map.of(
                    "user", Map.of(
                            "name", reservation.getCustomer().getUser().getName(),
                            "surname", reservation.getCustomer().getUser().getSurname(),
                            "email", reservation.getCustomer().getUser().getEmail()
                    ),
                    "phone", reservation.getCustomer().getPhone()
            ));
            response.put("id", reservation.getId());
            response.put("service", Map.of("name", reservation.getService().getName()));
            response.put("formattedCreatedAt", reservation.getFormattedCreatedAt());
            response.put("status", reservation.getStatus());
            response.put("formattedOrderTime", reservation.getFormattedOrderTime());
            response.put("notes", reservation.getNotes() != null ? reservation.getNotes() : "N/A");

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");
        }
    }
}