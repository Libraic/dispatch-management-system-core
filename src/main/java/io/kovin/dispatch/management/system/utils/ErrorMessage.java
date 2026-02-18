package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    /**
     * Global error messages.
     */
    public static final String CITIES_LOAD_ERROR = "Failed to load cities.";
    public static final String INVALID_ZIP_CODE = "The provided zip code=[%s] is invalid.";
    public static final String EMAIL_IN_USE = "The provided email is already in use.";
    public static final String EMAIL_IS_MANDATORY = "The e-mail cannot be empty.";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred. Please try again later.";
    public static final String INVALID_DATE_FORMAT = "The mileageDate=[%s] should be passed in dd-MM-YYYY format.";
    public static final String INVALID_PAGEABLE_ENTITY = "The entity=[%s] is not valid.";
    public static final String INVALID_SEARCH_CRITERIA = "The field=[%s] has an invalid format=[%s].";
    public static final String PASSWORD_INVALID_LENGTH = "The password has less than 8 characters.";
    public static final String PASSWORD_IS_MANDATORY = "The password is mandatory.";

    /**
     * Error messages related to Users.
     */
    public static final String BAD_ROLE = "The provided role=[%s] does not exist.";
    public static final String BAD_POSITION = "The provided position=[%s] does not exist.";
    public static final String BIRTH_DATE_IS_MANDATORY = "The birth mileageDate is mandatory.";
    public static final String BLANK_COMPANY = "You must choose a company.";
    public static final String EMPLOYMENT_DATE_IS_MANDATORY = "The employment mileageDate of the user is mandatory.";
    public static final String FIRST_NAME_IS_MANDATORY = "The first name is required.";
    public static final String INVALID_COMPANY = "The company with name=[%s] does not exist.";
    public static final String LAST_NAME_IS_MANDATORY = "The last name is required.";
    public static final String NEGATIVE_COMMISSION = "The commission cannot be negative.";
    public static final String SUPERVISOR_NOT_FOUND = "The supervisor with name=[%s] was not found.";

    /**
     * Error messages related to Company.
     */
    public static final String COMPANY_NOT_FOUND_BY_UUID = "The company with UUID=[%s] was not found.";
    public static final String MISSING_COMPANY_NAME = "The name of the company is missing.";
    public static final String MISSING_COMPANY_START_DATE = "The mileageDate depicting the collaboration with the company is missing.";

    /**
     * Error messages related to Drivers.
     */
    public static final String CITY_IS_MANDATORY = "The city the driver is making deliveries in is mandatory.";
    public static final String DRIVER_NOT_FOUND_BY_UUID = "The driver with UUID=[%s] was not found.";
    public static final String INVALID_DOCUMENT_STATUS = "The status of the documents is not valid.";
    public static final String INVALID_DRIVER_POSITION = "The position of the driver is not valid.";
    public static final String PHONE_NUMBER_IS_MANDATORY = "The phone number cannot be empty.";
    public static final String STATE_IS_MANDATORY = "The state the driver is making deliveries in is mandatory.";

    /**
     * Error messages related to Drivers Mileage.
     */
    public static final String BROKER_IS_MANDATORY = "Broker is required.";
    public static final String COMPANY_IS_MANDATORY = "The company is required.";
    public static final String DELIVERY_DATE_IS_MANDATORY = "The delivery date is required.";
    public static final String DELIVERY_LOCATION_IS_MANDATORY = "The delivery location is required.";
    public static final String DISPATCHER_IS_MANDATORY = "The dispatcher is required.";
    public static final String DRIVER_IS_MANDATORY = "The driver is required.";
    public static final String DRIVER_MILEAGE_NOT_FOUND_BY_UUID = "The driver with UUID=[%s] was not found.";
    public static final String MILEAGE_DATE_IS_MANDATORY = "The date of the mileage is required.";
    public static final String MILES_ARE_MANDATORY = "Miles are required.";
    public static final String NEGATIVE_MILES = "The miles cannot be negative.";
    public static final String NEGATIVE_REVENUE = "The revenue cannot be negative.";
    public static final String PICK_UP_DATE_IS_MANDATORY = "The pick-up date is required.";
    public static final String PICK_UP_LOCATION_IS_MANDATORY = "The pick-up location is required.";
    public static final String REVENUE_IS_MANDATORY = "The revenue is required.";
    public static final String START_DATE_IS_MANDATORY = "The start date of the mileage timeframe is required.";
    public static final String START_DATE_BEFORE_END_DATE = "The start date cannot be after the end date.";
    public static final String END_DATE_IS_MANDATORY = "The end date of the mileage timeframe is required.";

    /**
     * Error messages related to Trailer.
     */
    public static final String EQUIPMENT_SIZE_INVALID = "The equipment size is not valid.";
    public static final String EQUIPMENT_TYPE_IS_MANDATORY = "The equipment type cannot be empty.";
    public static final String MAX_WEIGHT_INVALID = "The max weight is not valid.";
    public static final String PALLET_CAPACITY_INVALID = "The pallet capacity is not valid.";
    public static final String TRAILER_NOT_FOUND_BY_UUID = "The trailer with UUID=[%s] was not found.";
    public static final String TRAILER_NUMBER_IS_MANDATORY = "The trailer number cannot be empty.";
    public static final String TRAILER_YEAR_INVALID = "The trailer year is not valid.";
    public static final String VIN_NUMBER_IS_MANDATORY = "The VIN number cannot be empty.";
    public static final String VIN_NUMBER_EXISTS = "The VIN number is taken.";

    /**
     * Error messages related to Trucks.
     */
    public static final String TRUCK_NOT_FOUND_BY_UUID = "The truck with UUID=[%s] was not found.";
    public static final String TRUCK_NUMBER_IS_MANDATORY = "The truck number cannot be empty.";
    public static final String TRUCK_YEAR_INVALID = "The truck year is not valid.";
    public static final String TRUCK_WEIGHT_INVALID = "The truck weight is not valid.";

    /**
     * Error messages related to KPIs.
     */
    public static final String INVALID_KPI_AGGREGATION_PERIOD = "%s is not a valid aggregation period.";
    public static final String INVALID_KPI_TARGET_ENTITY = "The KPIs are not available for [%s] entity.";
    public static final String INVALID_KPI_TYPE = "The [%s] KPI is not valid.";
    public static final String MISSING_KPIS = "At least one KPI must be provided.";

    /**
     * Error messages related to Dispatchers.
     */
    public static final String NAME_NOT_PROVIDED = "The name cannot be blank.";
    public static final String DISPATCHER_NOT_FOUND = "The dispatcher with UUID=[%s] was not found.";
}
