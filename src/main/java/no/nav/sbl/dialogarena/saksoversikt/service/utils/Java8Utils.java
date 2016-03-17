package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import com.sun.istack.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Java8Utils {


    public static <T> Optional<T> optional(@Nullable T value) {
        return Optional.ofNullable(value);
    }

    public static <T> Predicate<T> not(Predicate predicate) {
        return predicate.negate();
    }

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams).flatMap(Function.identity());
    }
}
