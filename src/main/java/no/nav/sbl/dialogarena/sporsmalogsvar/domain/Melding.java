package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

public class Melding implements Serializable {

    public final String id;
    public final Meldingstype meldingstype;
    public final DateTime opprettetDato;
    public String fritekst, temagruppe, kanal, traadId, navIdent, journalfortSaksId, journalfortTema,
            journalfortAvNavIdent, fnrBruker, kontorsperretEnhet;
    // TODO: Følg opp.
    public String sammensattNavnBruker = "TODO Hent fra context når Computas har satt det der.";
    public DateTime lestDato, journalfortDato;
    public boolean lest;
    public Status status;

    public Melding(String id, Meldingstype meldingstype, DateTime opprettetDato) {
        this.id = id;
        this.meldingstype = meldingstype;
        this.opprettetDato = opprettetDato;
    }

    public static final Transformer<Melding, String> TRAAD_ID = new Transformer<Melding, String>() {
        @Override
        public String transform(Melding melding) {
            return melding.traadId;
        }
    };

    public static final Comparator<Melding> NYESTE_FORST = new Comparator<Melding>() {
        @Override
        public int compare(Melding o1, Melding o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };

    public static final Comparator<Melding> ELDSTE_FORST = new Comparator<Melding>() {
        @Override
        public int compare(Melding o1, Melding o2) {
            return o1.opprettetDato.compareTo(o2.opprettetDato);
        }
    };
}
