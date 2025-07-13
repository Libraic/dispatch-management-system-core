package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedGroup.CITY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.EMAIL;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.FIRST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.LAST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.MAX_LEGAL_WEIGHT_CAPACITY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.PHONE_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.POSITION;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.STATE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_LENGTH;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_TYPE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRUCK_NUMBER;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.CITY_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DRIVER_POSITION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_MAX_LEGAL_WEIGHT_CAPACITY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TRAILER_LENGTH;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TRAILER_TYPE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PHONE_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.STATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRAILER_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRUCK_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorUtils.getItemsGroupFromImpactedGroupAndErrorMessage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.exception.ItemsGroup;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.entity.enums.TrailerType;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverValidationService {

    public void validateDriverCreation(CreateDriverRequest request) {
        log.info("Validating the request to create the driver.");
        List<ItemsGroup> itemsGroups = new ArrayList<>();
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            itemsGroups.add(getItemsGroup(FIRST_NAME, FIRST_NAME_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            itemsGroups.add(getItemsGroup(LAST_NAME, LAST_NAME_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.email())) {
            itemsGroups.add(getItemsGroup(EMAIL, EMAIL_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.phoneNumber())) {
            itemsGroups.add(getItemsGroup(PHONE_NUMBER, PHONE_NUMBER_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.trailerNumber())) {
            itemsGroups.add(getItemsGroup(TRAILER_NUMBER, TRAILER_NUMBER_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.truckNumber())) {
            itemsGroups.add(getItemsGroup(TRUCK_NUMBER, TRUCK_NUMBER_IS_MANDATORY));
        }

        if (BigDecimalUtils.isLessOrEqualToZeroSafe(request.maxLegalWeightCapacity())) {
            itemsGroups.add(getItemsGroup(MAX_LEGAL_WEIGHT_CAPACITY, INVALID_MAX_LEGAL_WEIGHT_CAPACITY));
        }

        if (TrailerType.from(request.trailerType()) == null) {
            itemsGroups.add(getItemsGroup(TRAILER_TYPE, INVALID_TRAILER_TYPE));
        }

        if (BigDecimalUtils.isLessOrEqualToZeroSafe(request.trailerLength())) {
            itemsGroups.add(getItemsGroup(TRAILER_LENGTH, INVALID_TRAILER_LENGTH));
        }

        if (DocumentStatus.from(request.documentsStatus()) == null) {
            itemsGroups.add(getItemsGroup(DOCUMENT_STATUS, INVALID_DOCUMENT_STATUS));
        }

        if (DriverPosition.from(request.position()) == null) {
            itemsGroups.add(getItemsGroup(POSITION, INVALID_DRIVER_POSITION));
        }

        if (StringUtil.isNullOrEmpty(request.state())) {
            itemsGroups.add(getItemsGroup(STATE, STATE_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.city())) {
            itemsGroups.add(getItemsGroup(CITY, CITY_IS_MANDATORY));
        }

        if (!itemsGroups.isEmpty()) {
            throw DispatchManagementSystemGroupException.of(itemsGroups, BAD_REQUEST);
        }
    }

    private ItemsGroup getItemsGroup(ImpactedGroup impactedGroup, String message) {
        log.error(message);
        return getItemsGroupFromImpactedGroupAndErrorMessage(impactedGroup, message);
    }
}
