package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponse<T, E> {

    T data;
    E error;

    public static <T, E> ApiResponse<T, E> fromData(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T, E> ApiResponse<T, E> fromError(E error) {
        return new ApiResponse<>(null, error);
    }
}
