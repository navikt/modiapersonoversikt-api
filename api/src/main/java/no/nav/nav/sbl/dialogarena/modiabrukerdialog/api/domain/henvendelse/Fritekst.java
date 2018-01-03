package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Optional.empty;

public class Fritekst implements Serializable {

    private final Person forfatter;
    private final String fritekst;
    private final DateTime opprettetDato;

    public static final Comparator<Fritekst> ELDSTE_FORST = Comparator.comparing(o -> o.opprettetDato);

    public Fritekst(String fritekst, Person forfatter, DateTime opprettetDato) {
        this.fritekst = fritekst;
        this.forfatter = forfatter;
        this.opprettetDato = opprettetDato;
    }

    public Fritekst(String fritekst) {
        this.fritekst = fritekst;
        this.forfatter = null;
        this.opprettetDato = null;
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
