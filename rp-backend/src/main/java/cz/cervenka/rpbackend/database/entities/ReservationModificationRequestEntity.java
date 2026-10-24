package cz.cervenka.rpbackend.database.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class ReservationModificationRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    private String requestedNotes;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity requestedService;

    private LocalDateTime requestedOrderTime;

    @Transient
    private String formattedRequestedOrderTime;

    public void formatRequestedOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedRequestedOrderTime = (requestedOrderTime != null) ? requestedOrderTime.format(formatter) : "N/A";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationEntity getReservation() {
        return reservation;
    }

    public void setReservation(ReservationEntity reservation) {
        this.reservation = reservation;
    }

    public String getRequestedNotes() {
        return requestedNotes;
    }

    public void setRequestedNotes(String requestedNotes) {
        this.requestedNotes = requestedNotes;
    }

    public ServiceEntity getRequestedService() {
        return requestedService;
    }

    public void setRequestedService(ServiceEntity requestedService) {
        this.requestedService = requestedService;
    }

    public LocalDateTime getRequestedOrderTime() {
        return requestedOrderTime;
    }

    public void setRequestedOrderTime(LocalDateTime requestedOrderTime) {
        this.requestedOrderTime = requestedOrderTime;
    }

    public String getFormattedRequestedOrderTime() {
        return formattedRequestedOrderTime;
    }

    public void setFormattedRequestedOrderTime(String formattedRequestedOrderTime) {
        this.formattedRequestedOrderTime = formattedRequestedOrderTime;
    }
}