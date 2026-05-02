package io.kovin.dispatch.management.system.model.request;

import java.util.UUID;

public record CreateDispatcherRequest(String name, String phoneNumber, UUID companyUuid) {
}
