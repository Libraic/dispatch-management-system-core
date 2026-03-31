package io.kovin.dispatch.management.system.model.response;

import java.time.LocalTime;

public record GetLoadStartingPointResponse(String location, LocalTime time) {
}
