package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedGroup.COMPANY_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.COMPANY_START_DATE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_NAME;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_START_DATE;
import static io.kovin.dispatch.management.system.utils.ErrorUtils.getItemsGroupFromImpactedGroupAndErrorMessage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ItemsGroup;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyValidationService {

    public void validateCompanyCreation(CreateCompanyRequest createCompanyRequest) {
        List<ItemsGroup> itemsGroups = new ArrayList<>();
        if (StringUtil.isNullOrEmpty(createCompanyRequest.name())) {
            log.error(MISSING_COMPANY_NAME);
            itemsGroups.add(getItemsGroupFromImpactedGroupAndErrorMessage(COMPANY_NAME, MISSING_COMPANY_NAME));
        }

        if (StringUtil.isNullOrEmpty(createCompanyRequest.startDate())) {
            String message = String.format(MISSING_COMPANY_START_DATE, createCompanyRequest.name());
            log.error(message);
            itemsGroups.add(getItemsGroupFromImpactedGroupAndErrorMessage(COMPANY_START_DATE, message));
        }

        if (!itemsGroups.isEmpty()) {
            throw DispatchManagementSystemGroupException.of(itemsGroups, BAD_REQUEST);
        }
    }
}
