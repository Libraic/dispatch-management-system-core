package cucumber.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class RestConstants {

    public static final String BASE_URL = "http://localhost:";
    public static final String BASE_COMPANIES_API_URL = "/api/companies";
    public static final String BASE_DRIVERS_API_URL = "/api/drivers";
}
