package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedGroup {

    /**
     * Fields related to User registration.
     */
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    BIRTH_DATE("birthDate"),
    EMPLOYMENT_DATE("employmentDate"),
    EMAIL("email"),
    PASSWORD("password"),
    SUPERVISOR("supervisor"),
    WORKLOADS("workloads"),

    /**
     * Fields related to Company registration.
     */
    COMPANY_NAME("name"),
    COMPANY_START_DATE("startDate"),

    /**
     * Fields related to Driver registration.
     */
    PHONE_NUMBER("phoneNumber"),
    TRUCK_NUMBER("truckNumber"),
    TRAILER_NUMBER("trailerNumber"),
    TRAILER_HEIGHT("trailerHeight"),
    MAX_LEGAL_WEIGHT_CAPACITY("maxLegalWeightCapacity"),
    TRAILER_TYPE("trailerType"),
    TRAILER_LENGTH("trailerLength"),
    DOCUMENT_STATUS("documentsStatus"),
    POSITION("position"),
    STATE("state"),
    CITY("city"),

    /**
     * Fields related to Mileage registration.
     */
    MILEAGE("mileage"),
    MILEAGE_DATA("mileageData"),
    COMPANY("company"),
    DRIVER("driver"),
    DISPATCHER("dispatcher"),
    ;

    private final String groupName;
}
