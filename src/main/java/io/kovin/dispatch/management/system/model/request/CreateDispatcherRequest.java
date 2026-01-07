package io.kovin.dispatch.management.system.model.request;

public record CreateDispatcherRequest(String name, String phoneNumber, String companyUuid) {
}
