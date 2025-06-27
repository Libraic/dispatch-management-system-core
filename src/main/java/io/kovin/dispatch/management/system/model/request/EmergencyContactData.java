package io.kovin.dispatch.management.system.model.request;

public record EmergencyContactData(
    String name,
    String relationship,
    String phone
) {
}
