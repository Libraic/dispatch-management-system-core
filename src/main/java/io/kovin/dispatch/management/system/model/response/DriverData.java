package io.kovin.dispatch.management.system.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DriverData {
    private String uuid;
    private String firstName;
    private String lastName;
    private String truckNumber;
    private String trailerNumber;
    private String email;
    private String phoneNumber;
    private String documentsStatus;
    private String location;
    private LocalDateTime createdAt;
}
