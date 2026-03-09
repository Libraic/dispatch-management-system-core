package io.kovin.dispatch.management.system.model.entity;

import java.time.LocalDate;
import io.kovin.dispatch.management.system.model.entity.enums.LocationType;
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
public class LocationData {

    private String location;
    private LocalDate date;
    private LocationType locationType;
    private Integer order;
}
