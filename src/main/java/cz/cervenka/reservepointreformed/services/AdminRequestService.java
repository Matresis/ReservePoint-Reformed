package cz.cervenka.reservepointreformed.services;

import cz.cervenka.reservepointreformed.database.entities.*;
import cz.cervenka.reservepointreformed.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationConfirmationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationModificationRequestRepository;
import cz.cervenka.reservepointreformed.database.repositories.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class AdminRequestService {

    private final ReservationRepository reservationRepository;
    private final ReservationModificationRequestRepository modificationRequestRepository;
    private final ReservationCancellationRequestRepository cancellationRequestRepository;
    private final ReservationConfirmationRequestRepository confirmationRequestRepository;
    private final EmailService emailService;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AdminRequestService(ReservationRepository reservationRepository,
                               ReservationModificationRequestRepository modificationRequestRepository,
                               ReservationCancellationRequestRepository cancellationRequestRepository,
                               ReservationConfirmationRequestRepository confirmationRequestRepository,
                               EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.cancellationRequestRepository = cancellationRequestRepository;
        this.confirmationRequestRepository = confirmationRequestRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void approveConfirmationRequest(Long id) {
        ReservationConfirmationRequestEntity request = confirmationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        reservation.setStatus(ReservationEntity.Status.CONFIRMED);
        reservationRepository.save(reservation);

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        confirmationRequestRepository.delete(request);
        emailService.sendReservationConfirmationEmail(reservation, customer, service);
    }

    @Transactional
    public void approveModificationRequest(Long id) {
        ReservationModificationRequestEntity request = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        reservation.setNotes(request.getRequestedNotes());
        reservation.setService(request.getRequestedService());
        reservation.setOrderedTime(request.getRequestedOrderTime());

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservationRepository.save(reservation);
        modificationRequestRepository.delete(request);

        emailService.sendReservationModificationApprovalEmail(reservation, customer, service);
    }

    @Transactional
    public void rejectConfirmationRequest(Long id, String reason) {
        ReservationConfirmationRequestEntity request = confirmationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservation.setStatus(ReservationEntity.Status.APPROVED);

        reservationRepository.save(reservation);
        confirmationRequestRepository.delete(request);

        emailService.sendReservationConfirmationRejectionEmail(reservation, customer, service, reason);
    }

    @Transactional
    public void rejectModificationRequest(Long id, String reason) {
        ReservationModificationRequestEntity request = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        modificationRequestRepository.delete(request);

        emailService.sendReservationModificationRejectionEmail(reservation, customer, service, reason);
    }

    @Transactional
    public void approveCancellation(Long id) {
        ReservationCancellationRequestEntity request = cancellationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReservationEntity reservation = request.getReservation();
        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        String reason = reservation.getNotes();


        cancellationRequestRepository.delete(request);
        reservationRepository.delete(reservation);

        emailService.sendReservationCancellationApprovalEmail(reservation, customer, service, reason);
    }
}
