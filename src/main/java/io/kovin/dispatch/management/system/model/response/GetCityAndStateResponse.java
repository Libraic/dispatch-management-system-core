package io.kovin.dispatch.management.system.model.response;

public record GetCityAndStateResponse(String zip, String city, String state, String timezone) {
}
