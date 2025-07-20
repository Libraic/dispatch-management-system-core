package io.kovin.dispatch.management.system.model.response.error;

public interface ErrorContainer {

    void addError(Error error);

    Object getValue();
}
