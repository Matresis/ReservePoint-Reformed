package cz.cervenka.rpbackend.controllers;

import cz.cervenka.rpbackend.database.entities.ReservationCancellationRequestEntity;
import cz.cervenka.rpbackend.database.entities.ReservationConfirmationRequestEntity;
import cz.cervenka.rpbackend.database.entities.ReservationEntity;
import cz.cervenka.rpbackend.database.entities.ReservationModificationRequestEntity;
import cz.cervenka.rpbackend.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.rpbackend.database.repositories.ReservationConfirmationRequestRepository;
import cz.cervenka.rpbackend.database.repositories.ReservationModificationRequestRepository;
import cz.cervenka.rpbackend.database.repositories.ReservationRepository;
import cz.cervenka.rpbackend.services.AdminRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
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
    public ResponseEntity<Map<String, Object>> viewRequests() {
        List<ReservationModificationRequestEntity> modificationRequests = modificationRequestRepository.findAll();
        List<ReservationCancellationRequestEntity> cancellationRequests = cancellationRequestRepository.findAll();
        List<ReservationConfirmationRequestEntity> confirmationRequests = confirmationRequestRepository.findAll();

        modificationRequests.forEach(ReservationModificationRequestEntity::formatRequestedOrderTime);

        List<ReservationEntity> reservations = reservationRepository.findAll();

        reservations.forEach(ReservationEntity::formatCreatedAt);
        reservations.forEach(ReservationEntity::formatOrderTime);

        Map<String, Object> responseData = Map.of(
                "modificationRequests", modificationRequests,
                "cancellationRequests", cancellationRequests,
                "confirmationRequests", confirmationRequests
        );

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/{id}/approve-confirmation")
    public ResponseEntity<Void> approveConfirmation(@PathVariable Long id) {
        requestService.approveConfirmationRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject-confirmation")
    public ResponseEntity<Void> rejectConfirmation(@PathVariable Long id, @RequestParam String reason) {
        requestService.rejectConfirmationRequest(id, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve-modification")
    public ResponseEntity<Void> approveModification(@PathVariable Long id) {
        requestService.approveModificationRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject-modification")
    public ResponseEntity<Void> rejectModification(@PathVariable Long id, @RequestParam String reason) {
        requestService.rejectModificationRequest(id, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve-cancellation")
    public ResponseEntity<Void> approveCancellation(@PathVariable Long id) {
        requestService.approveCancellation(id);
        return ResponseEntity.ok().build();
    }
}