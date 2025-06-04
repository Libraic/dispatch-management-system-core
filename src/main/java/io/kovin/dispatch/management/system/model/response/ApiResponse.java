package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponse<T> {

    T data;
    Error error;

    public static <T> ApiResponse<T> fromData(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> fromError(Error error) {
        return new ApiResponse<>(null, error);
    }
}
