package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedField {
    COMPANY("company"),
    COMMISSION("commission"),
    SUPERVISOR("supervisor"),

    DISPATCHER("dispatcher"),
    DRIVER("driver"),
    REVENUE("revenue"),
    MILES("miles"),

    /**
     * Fields related to Asset (Trailer/Truck) registration.
     */
    TRAILER_NUMBER("trailerNumber"),
    VIN_NUMBER("vinNumber"),
    TRAILER_YEAR("trailerYear"),
    EQUIPMENT_TYPE("equipmentType"),
    EQUIPMENT_SIZE("equipmentSize"),
    PALLET_CAPACITY("palletCapacity"),
    MAX_WEIGHT("maxWeight"),

    TRUCK_NUMBER("truckNumber"),
    TRUCK_YEAR("truckYear"),
    TRUCK_WEIGHT("weight"),
    ;

    private final String mappedField;
}
