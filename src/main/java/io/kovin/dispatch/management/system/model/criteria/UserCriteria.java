package io.kovin.dispatch.management.system.model.criteria;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserCriteria {

    String firstName;
    String nickname;
    String lastName;
    String fullName;
}
