package cz.cervenka.reservepointreformed.controllers;

import cz.cervenka.reservepointreformed.database.entities.ReservationCancellationRequestEntity;
import cz.cervenka.reservepointreformed.database.entities.ReservationConfirmationRequestEntity;
import cz.cervenka.reservepointreformed.database.entities.ReservationEntity;
import cz.cervenka.reservepointreformed.database.entities.ReservationModificationRequestEntity;
import cz.cervenka.reservepointreformed.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationConfirmationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationModificationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationRepository;
import cz.cervenka.reservepointreformed.services.AdminRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final AdminRequestService requestService;
    private final ReservationModificationRequestRepository modificationRequestRepository;
    private final ReservationCancellationRequestRepository cancellationRequestRepository;
    private final ReservationConfirmationRequestRepository confirmationRequestRepository;
    private final ReservationRepository reservationRepository;

    public AdminRequestController(AdminRequestService requestService,
                                  ReservationModificationRequestRepository modificationRequestRepository,
                                  ReservationCancellationRequestRepository cancellationRequestRepository,
                                  ReservationConfirmationRequestRepository confirmationRequestRepository,
                                  ReservationRepository reservationRepository) {
        this.requestService = requestService;
        this.modificationRequestRepository = modificationRequestRepository;
        this.cancellationRequestRepository = cancellationRequestRepository;
        this.confirmationRequestRepository = confirmationRequestRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String viewRequests(Model model) {
        List<ReservationModificationRequestEntity> modificationRequests = modificationRequestRepository.findAll();
        List<ReservationCancellationRequestEntity> cancellationRequests = cancellationRequestRepository.findAll();
        List<ReservationConfirmationRequestEntity> confirmationRequests = confirmationRequestRepository.findAll();

        modificationRequests.forEach(ReservationModificationRequestEntity::formatRequestedOrderTime);

        List<ReservationEntity> reservations = reservationRepository.findAll();

        reservations.forEach(ReservationEntity::formatCreatedAt);
        reservations.forEach(ReservationEntity::formatOrderTime);

        model.addAttribute("modificationRequests", modificationRequests);
        model.addAttribute("cancellationRequests", cancellationRequests);
        model.addAttribute("confirmationRequests", confirmationRequests);

        return "admin/requests";
    }

    @PostMapping("/{id}/approve-confirmation")
    public String approveConfirmation(@PathVariable Long id) {
        requestService.approveConfirmationRequest(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/reject-confirmation")
    public String rejectConfirmation(@PathVariable Long id, @RequestParam String reason) {
        requestService.rejectConfirmationRequest(id, reason);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/approve-modification")
    public String approveModification(@PathVariable Long id) {
        requestService.approveModificationRequest(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/reject-modification")
    public String rejectModification(@PathVariable Long id, @RequestParam String reason) {
        requestService.rejectModificationRequest(id, reason);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/approve-cancellation")
    public String approveCancellation(@PathVariable Long id) {
        requestService.approveCancellation(id);
        return "redirect:/admin/requests";
    }
}