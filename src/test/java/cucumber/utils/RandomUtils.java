package cucumber.utils;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtils {

    private static final Faker faker = new Faker();

    public static String generateLittleEndianDate() {
        String[] tokens = faker.date()
            .birthday()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()
            .split(ITConstants.HYPHEN);
        return String.format("%s-%s-%s", tokens[2], tokens[1], tokens[0]);
    }
}
