package io.kovin.dispatch.management.system.utils;

import io.kovin.dispatch.management.system.exception.ImpactedField;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.exception.FieldError;
import io.kovin.dispatch.management.system.model.response.error.GroupErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.ItemErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.FieldErrorResponse;
import io.kovin.dispatch.management.system.exception.ItemError;
import io.kovin.dispatch.management.system.exception.ItemsGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorUtils {

    public static ItemsGroup getItemsGroupFromImpactedGroupAndErrorMessage(
        ImpactedGroup impactedGroup,
        String errorMessage
    ) {
        ItemsGroup itemsGroup = ItemsGroup.ofGroupName(impactedGroup);
        itemsGroup.addItemError(ItemError.ofError(errorMessage));
        return itemsGroup;
    }

    public static List<GroupErrorResponse> getGroupsErrors(List<ItemsGroup> itemsGroups) {
        List<GroupErrorResponse> groupErrorResponses = new ArrayList<>();
        for (ItemsGroup itemsGroup : itemsGroups) {
            String impactedGroup = itemsGroup.getGroup().getGroupName();
            List<ItemErrorResponse> itemErrorResponses = new ArrayList<>();
            for (ItemError itemError : itemsGroup.getItemsErrors()) {
                String itemIdentifier = itemError.getItemIdentifier();
                List<FieldErrorResponse> fieldErrorResponses = new ArrayList<>();
                for (FieldError fieldError : itemError.getFieldsError()) {
                    fieldErrorResponses.add(new FieldErrorResponse(
                        Optional.ofNullable(fieldError.field()).map(ImpactedField::getMappedField).orElse(null),
                        fieldError.errorMessage())
                    );
                }
                itemErrorResponses.add(new ItemErrorResponse(itemIdentifier, fieldErrorResponses));
            }
            groupErrorResponses.add(new GroupErrorResponse(impactedGroup, itemErrorResponses));
        }

        return groupErrorResponses;
    }
}
