package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;

@Builder
public record CreateCompanyRequest(
    String name,
    String mcNumber,
    String address,
    String serviceDate,
    String startDate
) {
}
