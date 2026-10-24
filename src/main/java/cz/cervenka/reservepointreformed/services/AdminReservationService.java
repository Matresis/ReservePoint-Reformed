package cz.cervenka.reservepointreformed.services;

import cz.cervenka.reservepointreformed.database.entities.*;
import cz.cervenka.reservepointreformed.database.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final EmailService emailService;

    public AdminReservationService(ReservationRepository reservationRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ReservationEntity updateReservation(Long id, String status, String orderedTime, String notes) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        ReservationEntity.Status previousStatus = reservation.getStatus();
        ReservationEntity.Status newStatus = ReservationEntity.Status.valueOf(status);

        reservation.setStatus(newStatus);

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservation.setNotes(notes);
        reservationRepository.save(reservation);
        return reservation;
    }

    @Transactional
    public ReservationEntity approveReservation(Long id, String orderedTime) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        if (orderedTime != null && !orderedTime.isEmpty()) {
            LocalDateTime parsedTime = LocalDateTime.parse(orderedTime);

            boolean timeTaken = reservationRepository.existsByOrderedTimeAndIdNot(parsedTime, reservation.getId());
            if (timeTaken) {
                return null;
            }

            reservation.setOrderedTime(parsedTime);
        }

        reservation.setStatus(ReservationEntity.Status.APPROVED);
        reservationRepository.save(reservation);

        emailService.sendReservationApprovalEmail(reservation, customer, service);

        return reservation;
    }


    @Transactional
    public void rejectReservation(Long id, String notes) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservation.setNotes(notes);

        emailService.sendReservationRejectionEmail(reservation, customer, service, notes);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void deleteReservation(Long id, String notes) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        CustomerEntity customer = reservation.getCustomer();
        ServiceEntity service = reservation.getService();

        reservation.setNotes(notes);

        emailService.sendReservationCancellationEmail(reservation, customer, service, notes);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public ReservationEntity updateReservation(Long id, String orderedTime, String notes) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) return null;

        ReservationEntity reservation = reservationOpt.get();

        if (orderedTime != null && !orderedTime.isEmpty()) {
            reservation.setOrderedTime(LocalDateTime.parse(orderedTime));
        }

        reservation.setNotes(notes);
        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> getFilteredReservations(String name, String surname, String date, String serviceType) {
        LocalDate filterDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        List<ReservationEntity> reservations = reservationRepository.findFilteredReservations(name, surname, filterDate, serviceType);

        reservations.forEach(reservation -> {
            reservation.setFormattedCreatedAt(reservation.getCreatedAt().format(formatter));

            if (reservation.getOrderedTime() != null) {
                reservation.setFormattedOrderTime(reservation.getOrderedTime().format(formatter));
            } else {
                reservation.setFormattedOrderTime("N/A");
            }
        });

        return reservations;
    }
}