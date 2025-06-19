package io.kovin.dispatch.management.system.model.criteria;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SearchCriteria {

    String field;
    String operation;
    String value;
}
