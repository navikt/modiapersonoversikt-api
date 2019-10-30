package no.nav.kontrakter.consumer.utils;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import no.nav.kontrakter.consumer.fim.mapping.OppfolgingkontraktFIMMapper;

/**
 * Mapperklasse som benytter OppfolgingkontraktFIMMapper for custom mapping.
 */
public class OppfolgingskontraktMapper extends ConfigurableMapper {
    private static OppfolgingskontraktMapper instance = null;

    private OppfolgingskontraktMapper() {}

    public static OppfolgingskontraktMapper getInstance() {
        if (instance == null) {
            instance = new OppfolgingskontraktMapper();
        }

        return instance;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        OppfolgingkontraktFIMMapper.configure(mapperFactory, converterFactory);
    }
}
