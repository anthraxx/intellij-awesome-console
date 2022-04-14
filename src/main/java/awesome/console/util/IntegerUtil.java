package awesome.console.util;

import java.util.Optional;

public class IntegerUtil {
	public static Optional<Integer> parseInt(final String s) {
        try {
            return Optional.ofNullable(s).map(Integer::parseInt);
        } catch (final NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
