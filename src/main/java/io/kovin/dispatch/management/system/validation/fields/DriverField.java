package io.kovin.dispatch.management.system.validation.fields;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DriverField {

    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    DOCUMENT_STATUS("documentsStatus"),
    POSITION("position"),
    STATE("state"),
    CITY("city");

    private final String fieldName;
}
