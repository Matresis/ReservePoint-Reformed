package cz.cervenka.reservepointreformed.services;

import cz.cervenka.reservepointreformed.config.emails.EmailConfig;
import cz.cervenka.reservepointreformed.database.entities.CustomerEntity;
import cz.cervenka.reservepointreformed.database.entities.ReservationEntity;
import cz.cervenka.reservepointreformed.database.entities.ServiceEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final EmailConfig emailConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MMM.yyyy, HH:mm");
    private static final String adminEmail = "reservepointtp@gmail.com";

    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    @Transactional
    public void sendReservationCreationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Received: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We have received your reservation request for <strong>" + service.getName() + "</strong>.",
                "We will notify you once your reservation is approved.",
                "View Reservations",
                "http://141.147.9.34/reservations"
        );

        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Request",
                "A new reservation request has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong>",
                "Review Request",
                "http://141.147.9.34/admin/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfig.sendEmail(adminEmail, "New Reservation Request", adminEmailContent);
    }

    @Transactional
    public void sendReservationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Approved: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>We are pleased to inform you that your reservation for <strong>" + service.getName() + "</strong> has been approved.</p>",
                "<p>We scheduled your reservation on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong></p><br>"
                        + "<p>If you have any questions or the scheduled time does not comply, please contact us.</p>"
                        + "<p>If the scheduled time complies, please visit our website and confirm your reservation.</p>",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationConfirmationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Confirmed: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>We are pleased to confirm that your reservation for <strong>" + service.getName() + "</strong> has been successfully scheduled.</p>",
                "<p>Your appointment is set for: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong></p><br>"
                        + "<p>If you have any questions or need to make changes, please contact us.</p><br>"
                        + "<p>We look forward to serving you!</p>",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String notes) {
        String subject = "Reservation Rejected: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Unfortunately, your reservation for <strong>" + service.getName() + "</strong> made on: <strong>" + reservation.getCreatedAt().format(DATE_FORMATTER) + "</strong> has been rejected.",
                "The reason for rejection: <strong>" + notes + "</strong><br>"
                        + "<p>If you have any questions or you wish to recreate the reservation, please contact us or visit our website.</p>",
                "Make a New Reservation",
                "http://141.147.9.34/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationCancellationEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "We regret to inform you that your reservation for <strong>" + service.getName() + "</strong> scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> has been canceled.",
                "The reason for cancellation: <strong>" + reason + "</strong><br>"
                        + "<p>If you wish to reschedule, please visit our website.</p>",
                "Make a New Reservation",
                "http://141.147.9.34/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    public void sendReservationConfirmationRequestEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Confirmation Request: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Your request to confirm your reservation scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> for <strong>" + service.getName() + "</strong> has been sent.</p>",
                "You will be informed of the result once the request is processed.",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Confirmation Request",
                "A request for reservation confirmation has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong><br>"
                        + "Scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong>",
                "View Reservation",
                "http://141.147.9.34/admin/reservation/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfig.sendEmail(adminEmail, subject, adminEmailContent);
    }

    @Transactional
    public void sendReservationModificationRequestEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Request: " + service.getName();

        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Your request to modify your reservation for <strong>" + service.getName() + "</strong> has been sent.</p>",
                "<p>Requested modifications:</p><br>"
                        + "<ul>"
                        + "<li><strong>Service:</strong> " + reservation.getService().getName() + "</li>"
                        + "<li><strong>Date & Time:</strong> " + reservation.getOrderedTime().format(DATE_FORMATTER) + "</li>"
                        + "<li><strong>Notes:</strong> " + reservation.getNotes() + "</li>"
                        + "</ul><br>"
                        + "You will be informed of the result once the request is processed.",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Modification Request",
                "A request for reservation modification has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong><br>"
                        + "Scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong>",
                "Review Request",
                "http://141.147.9.34/admin/requests/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfig.sendEmail(adminEmail, subject, adminEmailContent);
    }

    @Transactional
    public void sendReservationCancellationRequestEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Cancellation Request: " + service.getName();

        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Your request to cancel your reservation scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> for <strong>" + service.getName() + "</strong> has been sent.</p>",
                "Reason for cancellation: <strong>" + reason + "</strong><br>"
                        + "You will be informed of the result once the request is processed.",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        String adminEmailContent = emailConfig.generateStyledEmail(
                "New Reservation Cancellation Request",
                "A request for reservation cancellation has been made by <strong>" + customer.getUser().getName() + " " + customer.getUser().getSurname() + "</strong>.",
                "Service: <strong>" + service.getName() + "</strong><br>"
                        + "Scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong>"
                        + "Reason: <strong>" + reason + "</strong>",
                "Review Request",
                "http://141.147.9.34/admin/requests/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
        emailConfig.sendEmail(adminEmail, subject, adminEmailContent);
    }

    @Transactional
    public void sendReservationConfirmationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your request for confirmation of your reservation for <strong>" + service.getName() + "</strong> has been rejected.",
                "The reason for rejection: <strong>" + reason + "</strong><br>"
                        + "<p>If you wish to reschedule or modify your reservation, please visit our website.</p>",
                "View Reservation",
                "http://141.147.9.34/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationModificationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service) {
        String subject = "Reservation Modification Approved: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Your request to modify your reservation for <strong>" + service.getName() + "</strong> has been approved.</p>",
                "<p>New details:</p><br>"
                        + "<ul>"
                        + "<li><strong>Service:</strong> " + reservation.getService().getName() + "</li>"
                        + "<li><strong>Date & Time:</strong> " + reservation.getOrderedTime().format(DATE_FORMATTER) + "</li>"
                        + "<li><strong>Notes:</strong> " + reservation.getNotes() + "</li>"
                        + "</ul>",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationModificationRejectionEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Modification Rejected: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "<p>Unfortunately, your request to modify your reservation for <strong>" + service.getName() + "</strong> has been rejected.</p>",
                "<p>Unaffected details:</p><br>"
                        + "<ul>"
                        + "<li><strong>Service:</strong> " + reservation.getService().getName() + "</li>"
                        + "<li><strong>Date & Time:</strong> " + reservation.getOrderedTime().format(DATE_FORMATTER) + "</li>"
                        + "<li><strong>Notes:</strong> " + reservation.getNotes() + "</li>"
                        + "</ul><br>"
                        + "<p>Reason: <strong>" + reason + "</strong><p>"
                        + "<p>If you have any questions, please contact us.</p>",
                "View Reservation",
                "http://141.147.9.34/reservations/" + reservation.getId()
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }

    @Transactional
    public void sendReservationCancellationApprovalEmail(ReservationEntity reservation, CustomerEntity customer, ServiceEntity service, String reason) {
        String subject = "Reservation Canceled: " + service.getName();
        String customerEmailContent = emailConfig.generateStyledEmail(
                "Dear " + customer.getUser().getName() + ",",
                "Your reservation for: <strong>" + service.getName() + "</strong> scheduled on: <strong>" + reservation.getOrderedTime().format(DATE_FORMATTER) + "</strong> has been successfully canceled per your request.",
                "The reason for cancellation: <strong>" + reason + "</strong><br>"
                        + "<p>If you wish to reschedule, please visit our website.</p>",
                "Make a New Reservation",
                "http://141.147.9.34/"
        );

        emailConfig.sendEmail(customer.getUser().getEmail(), subject, customerEmailContent);
    }
}