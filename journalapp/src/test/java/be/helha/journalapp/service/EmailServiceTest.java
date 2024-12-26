package be.helha.journalapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 *  This class contains unit tests for the {@link EmailService}.
 *  It uses Mockito to mock dependencies and JUnit 5 for testing.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    /**
     * Mocked dependency for sending emails.
     */
    @Mock
    private JavaMailSender mailSender;

    /**
     * Mocked MimeMessage for testing email content.
     */
    @Mock
    private MimeMessage mimeMessage;

    /**
     * Instance of the EmailService to be tested.
     */
    private EmailService emailService;

    /**
     * Sets up the test environment before each test method.
     * Initializes the EmailService with the mocked JavaMailSender and mocks the creation of a MimeMessage.
     */
    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    /**
     * Tests the successful sending of an email.
     *
     * @throws MessagingException If there is an error while creating or sending the email.
     */
    @Test
    void sendEmail_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "<h1>Test Content</h1>";

        // Act
        emailService.sendEmail(to, subject, content);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    /**
     * Tests the scenario where sending an email with a null recipient throws an IllegalArgumentException.
     */
    @Test
    void sendEmail_WithNullRecipient_ShouldThrowException() {
        // Arrange
        String subject = "Test Subject";
        String content = "<h1>Test Content</h1>";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(null, subject, content);
        });
    }


}