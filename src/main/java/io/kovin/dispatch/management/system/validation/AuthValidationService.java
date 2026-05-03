package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.model.error.AuthenticationErrorField.CREDENTIALS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_CREDENTIALS;

import ch.qos.logback.core.util.StringUtil;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemValidationException;
import io.kovin.dispatch.management.system.model.request.auth.AuthenticateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthValidationService {

    public void validateAuthenticationData(AuthenticateUserRequest authenticateUserRequest) {
        log.info("Validating the authentication data.");
        if (StringUtil.isNullOrEmpty(authenticateUserRequest.username()) || StringUtil.isNullOrEmpty(authenticateUserRequest.password())) {
            throw generateInvalidCredentialsException();
        }
    }

    public DispatchManagementSystemValidationException generateInvalidCredentialsException() {
        Map<String, String> errorFields = Map.of(CREDENTIALS.getField(), INVALID_CREDENTIALS);
        return DispatchManagementSystemValidationException.ofBadRequest(errorFields);
    }
}
