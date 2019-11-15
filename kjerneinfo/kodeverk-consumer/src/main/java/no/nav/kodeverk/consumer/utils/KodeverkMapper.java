package no.nav.kodeverk.consumer.utils;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.mapping.FIMKodeverkMapper;

public class KodeverkMapper extends ConfigurableMapper {
	private static KodeverkMapper instance = null;

	private KodeverkMapper() {}

	public static KodeverkMapper getInstance() {
		if (instance == null) {
			instance = new KodeverkMapper();
		}

		return instance;
	}

	@Override
	public void configure(MapperFactory mapperFactory) {
		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		FIMKodeverkMapper.configure(mapperFactory, converterFactory);
	}
}
