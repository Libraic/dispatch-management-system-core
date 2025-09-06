package io.kovin.dispatch.management.system.utils;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LocalDateUtils {

    public static final String LITTLE_ENDIAN_FORMAT_REGEX = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4}$";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static LocalDate parseLocalDate(String localDate) {
        if (StringUtil.isNullOrEmpty(localDate)) {
            return null;
        }

        try {
            return LocalDate.parse(localDate, FORMATTER);
        } catch (DateTimeParseException e) {
            String errorMessage = String.format(ErrorMessage.INVALID_DATE_FORMAT, localDate);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
