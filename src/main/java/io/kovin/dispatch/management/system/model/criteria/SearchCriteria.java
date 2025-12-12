package io.kovin.dispatch.management.system.model.criteria;

import io.kovin.dispatch.management.system.utils.constants.QueryConstants;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SearchCriteria {

    String field;
    String operation;
    String value;

    public boolean isJoinOperation() {
        return operation.equals(QueryConstants.JOIN);
    }
}
