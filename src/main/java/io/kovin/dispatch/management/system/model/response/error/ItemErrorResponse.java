package io.kovin.dispatch.management.system.model.response.error;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemErrorResponse {
    String itemIdentifier;
    List<FieldErrorResponse> groupItemFieldsErrors;
}
