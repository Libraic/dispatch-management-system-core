package io.kovin.dispatch.management.system.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ItemsGroup {

    private final ImpactedGroup group;
    private final List<ItemError> itemsErrors;

    private ItemsGroup(ImpactedGroup group) {
        this.group = group;
        itemsErrors = new ArrayList<>();
    }

    public void addItemError(ItemError itemError) {
        itemsErrors.add(itemError);
    }

    public boolean hasErrors() {
        return !itemsErrors.isEmpty();
    }

    public static ItemsGroup ofGroupName(ImpactedGroup group) {
        return new ItemsGroup(group);
    }
}
