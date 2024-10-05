package com.forever.services;

import com.forever.dtos.RegistrationRequest;
import com.forever.dtos.RegistrationResponse;

public interface RegistrationService {

    RegistrationResponse createUser(RegistrationRequest registrationRequest);
    void verifyUser(String email);
}
