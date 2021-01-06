package no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

abstract class Mapper {

    private HashMap<Class<?>, Function<Object, Object>> mappers = new HashMap<>();

    Mapper() {
        registrerCommonMappers();
    }

    private void registrerCommonMappers() {
        registerMapper(XMLGregorianCalendar.class, LocalDate.class, (dato) ->
                LocalDate.of(dato.getYear(), dato.getMonth(), dato.getDay())
        );
    }

    <S, T> void registerMapper(Class<S> from, Class<T> to, Function<S, T> mapperFunction) {
        this.mappers.put(from, (Function<Object, Object>)mapperFunction);
    }

    public <S, T> S map(T object) {
        return (S) map(singletonList(object)).get(0);
    }

    public <S, T> List<S> map(List<T> objects) {
        if (objects.isEmpty()) {
            return (List<S>)objects;
        }
        Class<T> fromClass = (Class<T>) objects.get(0).getClass();
        Function<T, S> mapperFunction = getMapper(fromClass)
                .map((function) -> (Function<T, S>)function)
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke mapper for klasse: " + fromClass.getCanonicalName()));

        return objects
                .stream()
                .map(mapperFunction)
                .collect(toList());
    }

    private <T> Optional<Function<Object, Object>> getMapper(Class<T> fromClass) {
        Optional<Function<Object, Object>> mapper = Optional.ofNullable(mappers.get(fromClass));
        if (mapper.isEmpty()) {
            return getMapper(fromClass.getSuperclass());
        }
        return mapper;
    }

}
