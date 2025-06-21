package io.kovin.dispatch.management.system.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ItemError {

    private final String itemIdentifier;
    private final List<FieldError> fieldsError;

    private ItemError() {
        this(null);
    }

    private ItemError(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
        fieldsError = new ArrayList<>();
    }

    public static ItemError ofItemIdentifier(String itemIdentifier) {
        return new ItemError(itemIdentifier);
    }

    public static ItemError ofError(String errorMessage) {
        ItemError itemError = new ItemError();
        itemError.addFieldError(null, errorMessage);
        return itemError;
    }

    public void addFieldError(ImpactedField field, String errorMessage) {
        fieldsError.add(new FieldError(field, errorMessage));
    }

    public boolean hasErrors() {
        return !fieldsError.isEmpty();
    }
}
