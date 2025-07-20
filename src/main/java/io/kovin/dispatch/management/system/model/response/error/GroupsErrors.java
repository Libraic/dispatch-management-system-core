package io.kovin.dispatch.management.system.model.response.error;

import java.util.HashMap;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.ImpactedField;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import lombok.Getter;

@Getter
public class GroupsErrors {

    private final Map<String, ErrorContainer> errors;

    public GroupsErrors() {
        errors = new HashMap<>();
    }

    public void addError(ImpactedGroup impactedGroup, String message) {
        String group = impactedGroup.getGroupName();
        Error error = new Error(group, message, null);
        ErrorContainer errorContainer = new SingleErrorContainer(error);
        errors.put(group, errorContainer);
    }

    public void addError(
        ImpactedGroup impactedGroup,
        ImpactedField impactedField,
        String message,
        String identifier
    ) {
        String group = impactedGroup.getGroupName();
        String field = impactedField.getMappedField();
        ErrorContainer errorContainer = errors.computeIfAbsent(group, (k) -> new MultipleErrorsContainer());
        Error error = new Error(field, message, identifier);
        errorContainer.addError(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
