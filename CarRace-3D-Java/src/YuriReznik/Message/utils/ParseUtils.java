package YuriReznik.Message.utils;

import java.util.Optional;

/**
 * Useful utilities for parsing
 */
public class ParseUtils {

    /**
     * Try parse Double value from string and return Optional
     * No Exceptions are thrown
     * @param input String contains double value
     * @return Optional of Double
     */
    public static Optional<Double> tryParseDouble(String input) {
        Optional<Double> result = Optional.empty();
        try {
            result = Optional.ofNullable(Double.parseDouble(input));
        } catch (Exception e) { }
        return result;
    }

    public static Optional<Long> tryParseLong(String input, int radix) {
        Optional<Long> result = Optional.empty();
        try {
            result = Optional.ofNullable(Long.parseLong(input, radix));
        } catch (Exception e) { }
        return result;
    }

    public static Optional<Long> tryParseLong(String input) {
        return ParseUtils.tryParseLong(input, 10);
    }
}
