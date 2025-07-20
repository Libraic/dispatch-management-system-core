package io.kovin.dispatch.management.system.model.response.error;

import java.util.ArrayList;
import java.util.List;

public class MultipleErrorsContainer implements ErrorContainer {

    private final List<Error> errors;

    public MultipleErrorsContainer() {
        errors = new ArrayList<>();
    }

    @Override
    public void addError(Error error) {
        errors.add(error);
    }

    @Override
    public Object getValue() {
        return errors;
    }
}
