package io.kovin.dispatch.management.system.model.request;

import java.util.List;

public record CreateUserRequest(
    String firstName,
    String lastName,
    String email,
    String password,
    String personalEmail,
    String birthDate,
    String employmentDate,
    String role,
    String position,
    String supervisorUuid,
    List<String> companiesUuids
) {
}
