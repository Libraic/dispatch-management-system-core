package io.kovin.dispatch.management.system.model.response;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.global.Mileage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DriverMileageDataOld {
    String uuid;
    DriverData driver;
    UserData dispatcher;
    String itemIdentifier;
    LocalDate startDate;
    LocalDate endDate;
    List<Mileage> mileageData;
}
