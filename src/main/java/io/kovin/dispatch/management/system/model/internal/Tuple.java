package io.kovin.dispatch.management.system.model.internal;

public record Tuple<L, M, R>(
    L left,
    M middle,
    R right
) {
}
