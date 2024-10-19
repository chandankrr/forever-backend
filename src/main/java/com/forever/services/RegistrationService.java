package com.forever.services;

import com.forever.dtos.RegistrationRequest;
import com.forever.dtos.RegistrationResponse;
import com.forever.entities.User;
import com.forever.repositories.UserRepository;
import com.forever.utils.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
        User existingUser = userRepository.findByEmail(registrationRequest.getEmail());

        if (existingUser != null) {
            return RegistrationResponse.builder()
                    .code(400)
                    .message("Email already exists!")
                    .build();
        }

        try {
            User user = User.builder()
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .email(registrationRequest.getEmail())
                    .password(passwordEncoder.encode(registrationRequest.getPassword()))
                    .enabled(false)
                    .provider("manual")
                    .verificationCode(VerificationCodeGenerator.generateCode())
                    .authorities(authorityService.getUserAuthority())
                    .build();

            userRepository.save(user);

            emailService.sendMail(user);

            return RegistrationResponse.builder()
                    .code(200)
                    .message("User created!")
                    .build();

        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage(), e.getCause());
        }
    }

    public void verifyUser(String email) {
        User user = userRepository.findByEmail(email);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
