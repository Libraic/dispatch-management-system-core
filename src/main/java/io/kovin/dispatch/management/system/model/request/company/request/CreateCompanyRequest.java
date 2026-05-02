package io.kovin.dispatch.management.system.model.request.company.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateCompanyRequest(
    String name,
    String mcNumber,
    String address,
    String email,
    String password,
    LocalDate serviceDate,
    LocalDate startDate,
    String timezone
) {
}
