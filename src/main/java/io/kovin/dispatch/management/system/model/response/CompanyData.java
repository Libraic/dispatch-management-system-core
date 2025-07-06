package io.kovin.dispatch.management.system.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyData {
    private String uuid;
    private String name;
    private String mcNumber;
    private String address;
    private String startDate;
}
