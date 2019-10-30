package no.nav.kodeverk.consumer.fim.kodeverk.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kode;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Term;
import org.joda.time.Chronology;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FIMKodeverkMapper {

    private FIMKodeverkMapper() {
    }

    public static void configure(MapperFactory mapperFactory, ConverterFactory converterFactory) {

        converterFactory.registerConverter(new CustomConverter<List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk>, Map<String, Kodeverk>>() {
            @Override
            public Map<String, Kodeverk> convert(List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk> from, Type<? extends Map<String, Kodeverk>> to, MappingContext mappingContext) {
                Map<String, Kodeverk> resultMap = new HashMap<>(from.size());
                for (no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk kodeverk : from) {
                    resultMap.put(kodeverk.getNavn(), mapperFacade.map(kodeverk, Kodeverk.class));
                }
                return resultMap;
            }
        });

        converterFactory.registerConverter(new CustomConverter<List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm>, Map<String, Term>>() {
            @Override
            public Map<String, Term> convert(List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm> from, Type<? extends Map<String, Term>> to, MappingContext mappingContext) {
                Map<String, Term> resultMap = new HashMap<>(from.size());
                for (no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm term : from) {
                    resultMap.put(term.getSpraak(), mapperFacade.map(term, Term.class));
                }
                return resultMap;
            }
        });

        converterFactory.registerConverter(new CustomConverter<List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode>, Map<String, Kode>>() {
            @Override
            public Map<String, Kode> convert(List<no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode> from, Type<? extends Map<String, Kode>> to, MappingContext mappingContext) {
                Map<String, Kode> resultMap = new HashMap<>(from.size());
                for (no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode kode : from) {
                    resultMap.put(kode.getNavn(), mapperFacade.map(kode, Kode.class));
                }
                return resultMap;
            }
        });

        converterFactory.registerConverter(new CustomConverter<DateMidnight, LocalDate>() {
            @Override
            public LocalDate convert(DateMidnight source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                if (source == null) {
                    return null;
                }

                return new LocalDate(source);
            }
        });

        mapperFactory.registerObjectFactory(new ObjectFactory<ISOChronology>() {
            @Override
            public ISOChronology create(Object source, MappingContext mappingContext) {
                if (source != null && source instanceof Chronology) {
                    if (source instanceof ISOChronology) {
                        return ((ISOChronology) source);
                    }
                    return ISOChronology.getInstance(((Chronology) source).getZone());
                }
                return ISOChronology.getInstanceUTC();
            }
        }, TypeFactory.valueOf(ISOChronology.class));
        mapperFactory.registerConcreteType(Chronology.class, ISOChronology.class);

        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLPeriode.class, Periode.class)
                .field("tom", "to")
                .field("fom", "from")
                .byDefault().toClassMap());
    }
}
