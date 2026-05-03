package io.kovin.dispatch.management.system.facade;

import io.kovin.dispatch.management.system.model.persistence.UserEntity;
import io.kovin.dispatch.management.system.model.request.auth.AuthenticateUserRequest;
import io.kovin.dispatch.management.system.service.UserService;
import io.kovin.dispatch.management.system.validation.AuthValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthValidationService authValidationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void authenticate(AuthenticateUserRequest request) {
        authValidationService.validateAuthenticationData(request);
        UserEntity user = userService.getUserByUsername(request.username());
        if (user == null) {
            throw authValidationService.generateInvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw authValidationService.generateInvalidCredentialsException();
        }
    }
}
