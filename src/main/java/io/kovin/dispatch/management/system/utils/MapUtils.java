package io.kovin.dispatch.management.system.utils;

import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtils {

    public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }
}
