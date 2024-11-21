package be.helha.journalapp.service;

import jakarta.mail.MessagingException; // Use jakarta.mail for Spring Boot 3+
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Inject JavaMailSender with constructor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with the specified subject and content to the recipient.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param content The HTML content of the email.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        // Create a MIME message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email properties
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // HTML content is enabled

        // Send the email
        mailSender.send(message);
    }
}
