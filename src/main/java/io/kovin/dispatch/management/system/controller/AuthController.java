package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.facade.AuthFacade;
import io.kovin.dispatch.management.system.model.request.auth.AuthenticateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Slf4j
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestBody AuthenticateUserRequest authenticateUserRequest) {
        log.info("A request to authenticate the user with username=[{}] was received.", authenticateUserRequest.username());
        authFacade.authenticate(authenticateUserRequest);
        return ResponseEntity.noContent().build();
    }
}
