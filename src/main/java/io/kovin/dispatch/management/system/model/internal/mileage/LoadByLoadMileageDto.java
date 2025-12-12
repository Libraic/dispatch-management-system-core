package io.kovin.dispatch.management.system.model.internal.mileage;

public record LoadByLoadMileageDto(
    String broker,
    String signature,
    double revenue,
    double miles
) {

    public static LoadByLoadMileageDto createEmptyLoadByLoadMileageDto() {
        return new LoadByLoadMileageDto(null, null, 0.0, 0.0);
    }
}
