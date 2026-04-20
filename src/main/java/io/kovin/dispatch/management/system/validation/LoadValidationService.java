package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.BROKER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOAD_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATIONS_ARE_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATIONS_CHRONOLOGICAL_ORDER;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATION_LABEL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATION_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATION_TYPE_ORDER_ERROR;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MILES_ARE_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_EMPTY_MILES;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_LOADED_MILES;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_REVENUE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.REVENUE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TIME_IS_MANDATORY;

import ch.qos.logback.core.util.StringUtil;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadValidationService {

    public void validateLoadUpsertion(UpsertLoadRequest request) {
        log.info("Validating the request to create the load.");

        if (StringUtil.isNullOrEmpty(request.loadUuid())) {
            if (request.loadedMiles() == null) {
                throw DispatchManagementSystemException.ofBadRequest(MILES_ARE_MANDATORY);
            }

            if (request.revenue() == null) {
                throw DispatchManagementSystemException.ofBadRequest(REVENUE_IS_MANDATORY);
            }

            if (StringUtil.isNullOrEmpty(request.broker())) {
                throw DispatchManagementSystemException.ofBadRequest(BROKER_IS_MANDATORY);
            }

            validateLocations(request.locations());
        }

        if (BigDecimalUtils.isNegative(request.loadedMiles())) {
            throw DispatchManagementSystemException.ofBadRequest(NEGATIVE_LOADED_MILES);
        }

        if (request.emptyMiles() != null && BigDecimalUtils.isNegative(request.emptyMiles())) {
            throw DispatchManagementSystemException.ofBadRequest(NEGATIVE_EMPTY_MILES);
        }

        if (BigDecimalUtils.isNegative(request.revenue())) {
            throw DispatchManagementSystemException.ofBadRequest(NEGATIVE_REVENUE);
        }
    }

    private void validateLocations(List<CreateLoadLocationRequest> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            throw DispatchManagementSystemException.ofBadRequest(LOCATIONS_ARE_MANDATORY);
        }

        for (int i = 0; i < locations.size(); i++) {
            CreateLoadLocationRequest current = locations.get(i);
            if (current.date() == null) {
                throw DispatchManagementSystemException.ofBadRequest(LOAD_DATE_IS_MANDATORY);
            }

            if (i > 0 && locations.get(i - 1).date().isAfter(current.date())) {
                throw DispatchManagementSystemException.ofBadRequest(LOCATIONS_CHRONOLOGICAL_ORDER);
            }

            if (current.location() == null) {
                throw DispatchManagementSystemException.ofBadRequest(LOCATION_MANDATORY);
            }

            if (current.label() == null) {
                throw DispatchManagementSystemException.ofBadRequest(LOCATION_LABEL_IS_MANDATORY);
            }

            LocationType locationType = LocationType.from(current.label());
            if (List.of(LocationType.DELIVERY, LocationType.PICK_UP).contains(locationType) && current.time() == null) {
                throw DispatchManagementSystemException.ofBadRequest(TIME_IS_MANDATORY);
            }
        }

        validateLocationTypesOrder(locations);
    }

    private void validateLocationTypesOrder(List<CreateLoadLocationRequest> locations) {
        String pickUpLabel = null;
        for (CreateLoadLocationRequest location : locations) {
            if (LocationType.PICK_UP.getType().equals(location.label())) {
                pickUpLabel = location.label();
            } else if (LocationType.DELIVERY.getType().equals(location.label()) && pickUpLabel == null) {
                throw DispatchManagementSystemException.ofBadRequest(LOCATION_TYPE_ORDER_ERROR);
            }
        }
    }
}
