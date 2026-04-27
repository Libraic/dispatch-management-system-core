package io.kovin.dispatch.management.system.model.request.company.response;

import lombok.Builder;

@Builder
public record GetCompanySettingsResponse(String timezone) {
}
