package io.kovin.dispatch.management.system.utils;

import java.util.Collection;

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
