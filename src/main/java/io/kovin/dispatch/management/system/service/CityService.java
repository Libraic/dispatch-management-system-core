package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.CITIES_LOAD_ERROR;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_ZIP_CODE;
import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.COLON;
import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.COMMA;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.LIKE;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.internal.CityData;
import io.kovin.dispatch.management.system.model.response.GetCityAndStateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CityService {

    private static final String USA_CITIES_FILEPATH = "/us-zips.csv";

    private List<CityData> cities;

    public CityService() {
        cities = new ArrayList<>();
    }

    /**
     * Loads city data from a predefined CSV file containing US city zip codes, cities, and states.
     * The data is parsed and transformed into a list of {@code CityData} objects, which is stored
     * for further usage within the service.
     * The process involves:
     * 1. Skipping the header row of the file.
     * 2. Reading and splitting each line into components.
     * 3. Filtering out lines that do not have exactly three components.
     * 4. Creating {@code CityData} objects from the parsed components.
     * In case of an error during file reading or processing, an unchecked
     * {@code DispatchManagementSystemException} is thrown with an appropriate error message.
     * This method is annotated with {@code @PostConstruct}, meaning it is invoked automatically
     * after the service bean is initialized by the Spring container.
     *
     * @throws DispatchManagementSystemException if an I/O error occurs or the data cannot be loaded.
     */
    @PostConstruct
    public void loadData() {
        log.info("Loading the cities data into the system.");
        try (InputStream is = getClass().getResourceAsStream(USA_CITIES_FILEPATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            cities = reader.lines()
                .skip(1)
                .map(line -> line.split(COMMA))
                .filter(parts -> parts.length == 3)
                .map(parts -> new CityData(parts[0].trim(), parts[1].trim(), parts[2].trim()))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw DispatchManagementSystemException.ofInternal(CITIES_LOAD_ERROR);
        }
    }

    /**
     * Searches through the list of cities and returns a filtered list of city data
     * whose zip code, city name, or state name starts with the specified prefix.
     * The queried prefix is sanitized before performing the search, and at most
     * 10 results are returned.
     *
     * @param prefix a string used to filter city data by prefix; it could match
     *               the beginning of a zip code, city name, or state name. If the
     *               prefix cannot be sanitized properly, an exception is thrown.
     * @return a list of {@code GetCityAndStateResponse} objects containing
     *         the zip code, city name, and state name of the matching cities.
     *         Returns an empty list if no matches are found.
     */
    public List<GetCityAndStateResponse> searchByPrefix(String prefix) {
        String sanitizedPrefix = sanitizePrefix(prefix);
        return cities.stream()
            .filter(cityData -> doesPrefixMatchTheCityData(cityData, sanitizedPrefix))
            .limit(10)
            .collect(Collectors.toSet())
            .stream()
            .map(cityData -> new GetCityAndStateResponse(cityData.zipCode(), cityData.city(), cityData.state()))
            .toList();
    }

    private String sanitizePrefix(String prefix) {
        if (prefix == null) {
            return null;
        }

        String[] tokens = prefix.split(COLON);
        if (tokens.length == 2 && tokens[0].equals(LIKE)) {
            return tokens[1];
        }

        throw DispatchManagementSystemException.of(String.format(INVALID_ZIP_CODE, prefix), HttpStatus.BAD_REQUEST);
    }

    private boolean doesPrefixMatchTheCityData(CityData cityData, String prefix) {
        return prefix == null
            || cityData.zipCode().startsWith(prefix)
            || cityData.city().toLowerCase().startsWith(prefix)
            || cityData.state().toLowerCase().startsWith(prefix);
    }
}
