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
