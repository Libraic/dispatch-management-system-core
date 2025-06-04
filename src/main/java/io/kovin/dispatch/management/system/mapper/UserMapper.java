package io.kovin.dispatch.management.system.mapper;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.SUPERVISOR_NOT_FOUND;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.Position;
import io.kovin.dispatch.management.system.model.entity.Role;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.repository.UserRepository;
import io.kovin.dispatch.management.system.utils.LocalDateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserEntity fromCreateUserRequestToUserEntity(CreateUserRequest request) {
        return UserEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .personalEmail(request.personalEmail())
            .birthDate(LocalDateUtils.parseLocalDate(request.birthDate()))
            .employmentDate(LocalDateUtils.parseLocalDate(request.employmentDate()))
            .role(Role.from(request.role()))
            .position(Position.from(request.position()))
            .supervisor(getSupervisor(request.supervisorUuid()))
            .build();
    }

    public UserData fromUserEntityToUserData(UserEntity userEntity) {
        return UserData.builder().uuid(userEntity.getUuid()).build();
    }

    private UserEntity getSupervisor(String supervisorUuid) {
        if (supervisorUuid == null) {
            return null;
        }

        Optional<UserEntity> supervisorOptional = userRepository.findByUuid(supervisorUuid);
        if (supervisorOptional.isEmpty()) {
            String errorMessage = String.format(SUPERVISOR_NOT_FOUND, supervisorUuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.BAD_REQUEST);
        }

        return supervisorOptional.get();
    }
}
