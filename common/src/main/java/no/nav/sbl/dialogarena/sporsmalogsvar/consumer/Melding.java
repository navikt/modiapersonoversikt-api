package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import org.apache.commons.collections15.Predicate;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class Melding implements Serializable {

    public String id, traadId, tema, overskrift, fritekst;
    public LocalDate opprettet;
    public Meldingstype type;

    public Melding withId(String id) {
        this.id = id;
        return this;
    }

    public Melding withTraadId(String traadId) {
        this.traadId = traadId;
        return this;
    }

    public Melding withTema(String tema) {
        this.tema = tema;
        return this;
    }

    public Melding withOverskrift(String overskrift) {
        this.overskrift = overskrift;
        return this;
    }

    public Melding withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public Melding withOpprettet(LocalDate opprettet) {
        this.opprettet = opprettet;
        return this;
    }

    public Melding withType(Meldingstype type) {
        this.type = type;
        return this;
    }

    public boolean erSvar() {
        return this.type == Meldingstype.SVAR;
    }

    public boolean erSporsmal() {
        return this.type == Meldingstype.SPORSMAL;
    }

    public static Predicate<Melding> harTraadId(final String traadId) {
        return new Predicate<Melding>() {
            @Override
            public boolean evaluate(Melding melding) {
                return traadId.equals(melding.traadId);
            }
        };
    }

}
