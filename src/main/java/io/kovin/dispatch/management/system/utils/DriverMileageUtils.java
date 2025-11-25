package io.kovin.dispatch.management.system.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverMileageUtils {

    public static Map<String, MileageData> createTimelineFromStartDate(LocalDate startDate) {
        LocalDate curr = startDate;
        Map<String, MileageData> timeline = new HashMap<>();
        for (int i = 0; i < 7; ++i) {
            timeline.put(curr.toString(), MileageData.builder().build());
            curr = curr.plusDays(1);
        }
        return timeline;
    }
}
