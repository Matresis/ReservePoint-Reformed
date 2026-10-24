package cz.cervenka.rpbackend.config.emails;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailConfig {
    private final JavaMailSender mailSender;

    public EmailConfig(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("reservepointtp@gmail.com"); // Sender is always ReservePoint
            helper.setTo(to); // Recipient can be anyone
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String generateStyledEmail(String greeting, String mainMessage, String additionalInfo, String buttonText, String buttonLink) {
        return "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; margin: 20px; padding: 20px; background-color: #f4f4f4; }"
                + ".email-container { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                + ".email-content { color: #333; font-size: 16px; }"
                + ".button { display: inline-block; padding: 10px 15px; margin-top: 15px; background: #007bff; color: white !important; text-decoration: none !important; border-radius: 5px; }"
                + ".signature { margin-top: 20px; font-size: 14px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='email-container'>"
                + "<div class='email-content'>"
                + "<p>" + greeting + "</p>"
                + "<p>" + mainMessage + "</p>"
                + "<p>" + additionalInfo + "</p>"
                + "<a class='button' href='" + buttonLink + "'>" + buttonText + "</a>"
                + "<p class='signature'>Best regards,<br><strong>ReservePoint Team</strong><br><em>reservepointtp@gmail.com</em></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}