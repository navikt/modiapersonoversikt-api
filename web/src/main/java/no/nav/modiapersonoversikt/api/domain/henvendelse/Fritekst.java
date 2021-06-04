package no.nav.modiapersonoversikt.api.domain.henvendelse;

import no.nav.modiapersonoversikt.api.domain.Person;
import no.nav.modiapersonoversikt.api.domain.Saksbehandler;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Optional.empty;

public class Fritekst implements Serializable {

    private final Person forfatter;
    private final String fritekst;
    private final DateTime dato;

    public static final Comparator<Fritekst> ELDSTE_FORST = Comparator.comparing(o -> o.dato);

    private String filtrererBortUgyldigXML(String tekst) {
        if (tekst != null) {
            return tekst.replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", "");
        }
        return null;
    }

    public Fritekst(String fritekst, Person forfatter, DateTime dato) {
        this.fritekst = filtrererBortUgyldigXML(fritekst);
        this.forfatter = forfatter;
        this.dato = dato;
    }

    public Fritekst(String fritekst) {
        this.fritekst = filtrererBortUgyldigXML(fritekst);
        this.forfatter = null;
        this.dato = null;
    }

    public String getFritekst() {
        return fritekst;
    }

    public Optional<Saksbehandler> getSaksbehandler() {
        if (forfatter instanceof Saksbehandler) {
            return Optional.of((Saksbehandler) forfatter);
        } else {
            return empty();
        }
    }

}
