package no.nav.modiapersonoversikt.legacy.sak.utils;

import java.util.function.Function;
import java.util.stream.Stream;

public class Java8Utils {
    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams).flatMap(Function.identity());
    }
}
