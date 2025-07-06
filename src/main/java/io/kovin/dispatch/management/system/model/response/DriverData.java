package io.kovin.dispatch.management.system.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DriverData {
    private String uuid;
    private String firstName;
    private String lastName;
    private String truckNumber;
    private String trailerNumber;
    private String email;
    private String phoneNumber;
}
