package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetCompaniesRequest {

    int page;
    int size;
}
