package be.helha.journalapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PasswordEncoderController {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint pour encoder un mot de passe brut.
     *
     * @param rawPassword Le mot de passe brut à encoder.
     * @return Le mot de passe encodé (hashé).
     */
    @PostMapping("/encode-password")
    public String encodePassword(@RequestParam String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
