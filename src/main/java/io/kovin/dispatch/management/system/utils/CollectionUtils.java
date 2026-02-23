package io.kovin.dispatch.management.system.utils;

import java.util.Collection;

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> void addIfNotEmpty(Collection<T> collection, T element) {
        if (element != null) {
            collection.add(element);
        }
    }
}
