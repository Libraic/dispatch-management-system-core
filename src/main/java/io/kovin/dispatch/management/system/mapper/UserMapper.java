package io.kovin.dispatch.management.system.mapper;

import static io.kovin.dispatch.management.system.exception.ImpactedArea.EMPLOYMENT_INFORMATION;
import static io.kovin.dispatch.management.system.exception.ImpactedField.SUPERVISOR;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.SUPERVISOR_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.Position;
import io.kovin.dispatch.management.system.model.entity.Role;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateSupervisorRequest;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.repository.UserRepository;
import io.kovin.dispatch.management.system.utils.LocalDateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            .nickname(request.nickname())
            .fullName(getFullName(request.firstName(), request.nickname(), request.lastName()))
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .personalEmail(request.personalEmail())
            .birthDate(LocalDateUtils.parseLocalDate(request.birthDate()))
            .employmentDate(LocalDateUtils.parseLocalDate(request.employmentDate()))
            .role(Role.from(request.role()))
            .position(Position.from(request.position()))
            .supervisor(getSupervisor(request.supervisor()))
            .build();
    }

    public UserData fromUserEntityToUserData(UserEntity userEntity) {
        return UserData.builder()
            .uuid(userEntity.getUuid())
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .nickname(userEntity.getNickname())
            .build();
    }

    private UserEntity getSupervisor(CreateSupervisorRequest supervisor) {
        if (supervisor == null) {
            return null;
        }

        String supervisorUuid = supervisor.uuid();
        if (supervisorUuid == null) {
            return null;
        }

        Optional<UserEntity> supervisorOptional = userRepository.findByUuid(supervisorUuid);
        if (supervisorOptional.isEmpty()) {
            String errorMessage = String.format(SUPERVISOR_NOT_FOUND, supervisor.fullName());
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, BAD_REQUEST, SUPERVISOR, EMPLOYMENT_INFORMATION);
        }

        return supervisorOptional.get();
    }

    private String getFullName(String firstName, String nickname, String lastName) {
        if (StringUtil.isNullOrEmpty(nickname)) {
            return String.format("%s %s", firstName, lastName);
        }

        return String.format("%s %s %s", firstName, nickname, lastName);
    }
}
