package io.kovin.dispatch.management.system.model.request;

public record CreateCompanyRequest(
    String name,
    String mcNumber,
    String address,
    String serviceDate,
    String startDate
) {
}
