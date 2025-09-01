package io.kovin.dispatch.management.system.model.response;

import java.util.List;
import io.kovin.dispatch.management.system.model.global.Mileage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DriverMileageData {
    String uuid;
    DriverData driver;
    UserData dispatcher;
    String itemIdentifier;
    List<Mileage> mileageData;
}
