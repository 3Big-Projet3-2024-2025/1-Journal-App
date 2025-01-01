package be.helha.journalapp.controller;

import be.helha.journalapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> emailRequest) {
        String to = emailRequest.get("to");
        String subject = emailRequest.get("subject");
        String content = emailRequest.get("content");

        Map<String, String> response = new HashMap<>();

        try {
            emailService.sendEmail(to, subject, content);
            response.put("message", "Email sent successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error sending email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
