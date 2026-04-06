package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadValidationService {

    public void validateLoadUpsertion(UpsertLoadRequest request) {
        log.info("Validating the request to create the load.");

        // These validations are only for the creation of the load.
        if (StringUtil.isNullOrEmpty(request.loadUuid())) {
            if (request.miles() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.MILES_ARE_MANDATORY);
            }

            if (request.revenue() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.REVENUE_IS_MANDATORY);
            }

            if (StringUtil.isNullOrEmpty(request.broker())) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.BROKER_IS_MANDATORY);
            }

            validateLocations(request.locations());
        }

        if (BigDecimalUtils.isNegative(request.miles())) {
            throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.NEGATIVE_MILES);
        }

        if (BigDecimalUtils.isNegative(request.revenue())) {
            throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.NEGATIVE_REVENUE);
        }
    }

    private void validateLocations(List<CreateLoadLocationRequest> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.LOCATIONS_ARE_MANDATORY);
        }

        for (int i = 0; i < locations.size(); i++) {
            CreateLoadLocationRequest current = locations.get(i);
            if (current.date() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.LOAD_DATE_IS_MANDATORY);
            }

            if (i > 0 && locations.get(i - 1).date().isAfter(current.date())) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.LOCATIONS_CHRONOLOGICAL_ORDER);
            }

            if (current.location() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.LOCATION_MANDATORY);
            }

            if (current.label() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.LOCATION_LABEL_IS_MANDATORY);
            }

            LocationType locationType = LocationType.from(current.label());
            if (List.of(LocationType.DELIVERY, LocationType.PICK_UP).contains(locationType) && current.time() == null) {
                throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.TIME_IS_MANDATORY);
            }
        }
    }
}
