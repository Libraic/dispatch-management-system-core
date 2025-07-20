package io.kovin.dispatch.management.system.model.response.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SingleErrorContainer implements ErrorContainer {
    private final Error error;

    @Override
    public void addError(Error error) {

    }

    @Override
    public Object getValue() {
        return error;
    }


}
