package io.kovin.dispatch.management.system.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorField {

    CREDENTIALS("credentials");

    private final String field;
    }
