package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

@JsonDeserialize(using = Sak.SakDeserializer.class)
@JsonSerialize(using = Sak.SakSerializer.class)
public class Sak implements Serializable, Comparable<Sak> {

    public Optional<String> saksId = none();
    public Optional<String> fagsystemSaksId = none();
    public String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak = false, finnesIPsak = false;

    public static final String TEMAKODE_OPPFOLGING = "OPP";
    public static final String TEMAKODE_KLAGE_ANKE = "KLA";
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String SAKSTYPE_MED_FAGSAK = "MFS";
    public static final String FAGSYSTEMKODE_ARENA = "AO01";
    public static final String FAGSYSTEMKODE_PSAK = "PSAK";

    public static final String GODKJENT_FAGSYSTEM_FOR_GENERELLE = "FS22";
    public static final List<String> GODKJENTE_TEMA_FOR_GENERELLE = unmodifiableList(asList("AGR", "FUL", "GEN", "KTR", "STO", "SER", "SYM", "TRK", "TRY", "VEN", "UFM", TEMAKODE_OPPFOLGING));
    public static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = unmodifiableList(asList(FAGSYSTEMKODE_ARENA, FAGSYSTEMKODE_PSAK, "IT01", "OEBS", "V2", "AO11"));

    public boolean isSakstypeForVisningGenerell() {
        return SAKSTYPE_GENERELL.equals(sakstype);
    }

    public static final Transformer<Sak, String> TEMAKODE = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.temaKode;
        }
    };

    public static final Transformer<Sak, Boolean> IS_GENERELL_SAK = new Transformer<Sak, Boolean>() {
        @Override
        public Boolean transform(Sak sak) {
            return sak.isSakstypeForVisningGenerell();
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENT_FAGSYSTEM_FOR_GENERELLE.equals(sak.fagsystemKode);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_TEMA_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_TEMA_FOR_GENERELLE.contains(sak.temaKode);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode);
        }
    };

    public static Predicate<Sak> harTemaKode(final String temaKode) {
        return new Predicate<Sak>() {
            @Override
            public boolean evaluate(Sak sak) {
                return temaKode.equals(sak.temaKode);
            }
        };
    }

    public static final Predicate<Sak> IS_ARENA_OPPFOLGING = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return TEMAKODE_OPPFOLGING.equals(sak.temaKode)
                    && FAGSYSTEMKODE_ARENA.equals(sak.fagsystemKode)
                    && SAKSTYPE_MED_FAGSAK.equals(sak.sakstype);
        }
    };

    public String getOpprettetDatoFormatert() {
        return opprettetDato == null ? "" : Datoformat.langUtenLiteral(opprettetDato);
    }

    public String getSaksIdVisning() {
        if (fagsystemSaksId.isSome()) {
            return fagsystemSaksId.get();
        } else if (saksId.isSome()) {
            return saksId.get();
        } else {
            return "";
        }
    }

    @Override
    public int compareTo(Sak other) {
        return other.opprettetDato.compareTo(opprettetDato);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Sak sak = (Sak) obj;
        if (saksId.isSome() && sak.saksId.isSome() && saksId.get().equals(sak.saksId.get())) {
            return true;
        } else {
            return temaKode != null && sak.temaKode != null
                    && fagsystemKode != null && sak.fagsystemKode != null
                    && sakstype != null && sak.sakstype != null
                    && temaKode.equals(sak.temaKode)
                    && fagsystemKode.equals(sak.fagsystemKode)
                    && sakstype.equals(sak.sakstype);
        }
    }

    @Override
    public int hashCode() {
        if (saksId.isSome()) {
            return saksId.get().hashCode();
        } else if (temaKode != null
                && fagsystemKode != null
                && sakstype != null) {
            return temaKode.hashCode() * fagsystemKode.hashCode() * sakstype.hashCode();
        } else {
            return 0;
        }
    }

//    Optional<String> saksId = none();
//    Optional<String> fagsystemSaksId = none();
//    String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
//    DateTime opprettetDato;
//    Boolean finnesIGsak = false, finnesIPsak = false;

    static class SakDeserializer extends JsonDeserializer<Sak> {

        @Override
        public Sak deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            Sak sak = new Sak();
            sak.saksId = optional(node.get("saksId").textValue());
            sak.fagsystemSaksId = optional(node.get("fagsystemSaksId").textValue());
            sak.temaKode = node.get("temaKode").textValue();
            sak.temaNavn = node.get("temaNavn").textValue();
            sak.fagsystemKode = node.get("fagsystemKode").textValue();
            sak.fagsystemNavn = node.get("fagsystemNavn").textValue();
            sak.sakstype = node.get("sakstype").textValue();
            sak.finnesIGsak = node.get("finnesIGsak").booleanValue();
            sak.finnesIPsak = node.get("finnesIPsak").booleanValue();

            return sak;
        }
    }

    static class SakSerializer extends JsonSerializer<Sak> {

        @Override
        public void serialize(Sak sak, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("saksId", sak.saksId.getOrElse(null));
            jsonGenerator.writeStringField("saksIdVisning", sak.getSaksIdVisning());
            jsonGenerator.writeStringField("fagsystemSaksId", sak.fagsystemSaksId.getOrElse(null));
            jsonGenerator.writeStringField("temaKode", sak.temaKode);
            jsonGenerator.writeStringField("temaNavn", sak.temaNavn);
            jsonGenerator.writeStringField("fagsystemKode", sak.fagsystemKode);
            jsonGenerator.writeStringField("fagsystemNavn", sak.fagsystemNavn);
            jsonGenerator.writeStringField("sakstype", sak.sakstype);
            jsonGenerator.writeStringField("opprettetDatoFormatert", sak.getOpprettetDatoFormatert());
            jsonGenerator.writeStringField("finnesIGsak", sak.finnesIGsak.toString());
            jsonGenerator.writeStringField("finnesIPsak", sak.finnesIPsak.toString());
            jsonGenerator.writeEndObject();
        }
    }

}
