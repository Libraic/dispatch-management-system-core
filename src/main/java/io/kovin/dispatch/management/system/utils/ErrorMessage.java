package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    /**
     * Common error messages.
     */
    public static final String CITIES_LOAD_ERROR = "Failed to load cities.";
    public static final String EXTERNAL_SERVICE_ERROR = "Failed to call external service.";
    public static final String INVALID_ZIP_CODE = "The provided zip code=[%s] is invalid.";
    public static final String EMAIL_IS_MANDATORY = "The e-mail cannot be empty.";
    public static final String END_DATE_IS_MANDATORY = "The start date is required.";
    public static final String FIRST_NAME_IS_MANDATORY = "The first name is required.";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred. Please try again later.";
    public static final String INVALID_SEARCH_CRITERIA = "The field=[%s] has an invalid format=[%s].";
    public static final String LAST_NAME_IS_MANDATORY = "The last name is required.";
    public static final String LOCATION_MANDATORY = "The location is required.";
    public static final String PASSWORD_INVALID_LENGTH = "The password has less than 8 characters.";
    public static final String PASSWORD_IS_MANDATORY = "The password is mandatory.";
    public static final String START_DATE_IS_MANDATORY = "The start date is required.";

    public static final String INVALID_CREDENTIALS = "The provided credentials are invalid.";

    /**
     * Error messages related to Company.
     */
    public static final String COMPANY_NOT_FOUND_BY_UUID = "The company with UUID=[%s] was not found.";
    public static final String MISSING_COMPANY_NAME = "The name of the company is missing.";
    public static final String MISSING_COMPANY_START_DATE = "The loadDate depicting the collaboration with the company is missing.";

    /**
     * Error messages related to Drivers.
     */
    public static final String CITY_IS_MANDATORY = "The city the driver is making deliveries in is mandatory.";
    public static final String INVALID_DOCUMENT_STATUS = "The status of the documents is not valid.";
    public static final String INVALID_DRIVER_POSITION = "The position of the driver is not valid.";
    public static final String INVALID_TIMEZONE = "The timezone is not valid.";
    public static final String PHONE_NUMBER_IS_MANDATORY = "The phone number cannot be empty.";
    public static final String STATE_IS_MANDATORY = "The state the driver is making deliveries in is mandatory.";

    /**
     * Error messages related to Loads.
     */
    public static final String BROKER_IS_MANDATORY = "Broker is required.";
    public static final String LOCATIONS_CHRONOLOGICAL_ORDER = "The locations must be in chronological order.";
    public static final String LOAD_NOT_FOUND = "The load was not found.";
    public static final String LOAD_NOT_FOUND_BY_UUID = "The load with UUID=[%s] was not found.";
    public static final String DELIVERY_LOCATION_MISSING = "The delivery location is missing.";
    public static final String DRIVER_DISPATCHER_RELATION_NOT_FOUND_BY_UUID = "The Driver-Dispatcher relation with UUID=[%s] was not found.";
    public static final String INVALID_LOAD_STATUS_BE = "The load status=[%s] is invalid.";
    public static final String INVALID_LOAD_STATUS_CLIENT = "Invalid load status.";
    public static final String LOCATION_LABEL_IS_MANDATORY = "The location label is required.";
    public static final String LOCATION_TYPE_ORDER_ERROR = "The order of the locations is not valid.";
    public static final String LOCATIONS_ARE_MANDATORY = "The locations are required.";
    public static final String LOAD_DATE_IS_MANDATORY = "The date of the load is required.";
    public static final String LOAD_NUMBER_IS_MANDATORY = "The load number is required.";
    public static final String MILES_ARE_MANDATORY = "Miles are required.";
    public static final String NEGATIVE_LOADED_MILES = "The loaded miles cannot be negative.";
    public static final String NEGATIVE_EMPTY_MILES = "The empty miles cannot be negative.";
    public static final String NEGATIVE_REVENUE = "The revenue cannot be negative.";
    public static final String PICK_UP_LOCATION_MISSING = "The pick up location is missing.";
    public static final String REVENUE_IS_MANDATORY = "The revenue is required.";
    public static final String START_DATE_BEFORE_END_DATE = "The start date cannot be after the end date.";
    public static final String TIME_IS_MANDATORY = "The time is required for this location type.";

    /**
     * Error messages related to Vehicle Maintenance Records.
     */
    public static final String VEHICLE_MAINTENANCE_RECORD_NOT_FOUND_BY_UUID = "The Vehicle Maintenance Record with UUID=[%s] was not found.";
    public static final String VEHICLE_MAINTENANCE_RECORD_NOT_FOUND = "The Vehicle Maintenance Record was not found.";

    /**
     * Error messages related to Days Off Period.
     */
    public static final String DAYS_OFF_PERIOD_NOT_FOUND_BY_UUID = "The Days Off Period with UUID=[%s] was not found.";
    public static final String DAYS_OFF_PERIOD_NOT_FOUND = "The Days Off Period was not found.";

    /**
     * Error messages related to Trailer.
     */
    public static final String EQUIPMENT_SIZE_INVALID = "The equipment size is not valid.";
    public static final String EQUIPMENT_TYPE_IS_MANDATORY = "The equipment type cannot be empty.";
    public static final String MAX_WEIGHT_INVALID = "The max weight is not valid.";
    public static final String PALLET_CAPACITY_INVALID = "The pallet capacity is not valid.";
    public static final String TRAILER_NUMBER_IS_MANDATORY = "The trailer number cannot be empty.";
    public static final String TRAILER_YEAR_INVALID = "The trailer year is not valid.";
    public static final String VIN_NUMBER_IS_MANDATORY = "The VIN number cannot be empty.";
    public static final String VIN_NUMBER_EXISTS = "The VIN number is taken.";

    /**
     * Error messages related to Trucks.
     */
    public static final String TRUCK_NUMBER_IS_MANDATORY = "The truck number cannot be empty.";
    public static final String TRUCK_YEAR_INVALID = "The truck year is not valid.";
    public static final String TRUCK_WEIGHT_INVALID = "The truck weight is not valid.";

    /**
     * Error messages related to Dispatchers.
     */
    public static final String DISPATCHER_NOT_FOUND = "The dispatcher with UUID=[%s] was not found.";
    public static final String NAME_NOT_PROVIDED = "The name cannot be blank.";
}
