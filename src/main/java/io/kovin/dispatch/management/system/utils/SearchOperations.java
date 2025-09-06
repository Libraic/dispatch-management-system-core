package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchOperations {

    public static final String LIKE = "like";
    public static final String EQUAL = "eq";
    public static final String JOIN = "join";
    public static final String GREATER_OR_EQUAL = "gte";
    public static final String LESS_OR_EQUAL = "lte";
}
