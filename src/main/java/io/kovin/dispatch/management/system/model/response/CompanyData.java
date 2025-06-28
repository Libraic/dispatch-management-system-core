package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

@Builder
public record CompanyData(
    String uuid,
    String name,
    String mcNumber,
    String address,
    String startDate
) {
}
