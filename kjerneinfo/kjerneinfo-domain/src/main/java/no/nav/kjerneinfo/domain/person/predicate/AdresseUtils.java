package no.nav.kjerneinfo.domain.person.predicate;

public class AdresseUtils {

    public static String spaceAppend(String... input) {
        return seperatorAppend(" ", input);
    }

    public static String append(String... input) {
        return seperatorAppend("", input);
    }

    public static String seperatorAppend(String seperator, String... input) {
        StringBuilder builder = new StringBuilder();
        for (String string : input) {
            if (string != null) {
                builder.append(string).append(seperator);
            }
        }
        String string = builder.toString();
		int endIndex = string.length() - seperator.length();
		return endIndex <= 0 ? "" : string.substring(0, endIndex);
    }
}
