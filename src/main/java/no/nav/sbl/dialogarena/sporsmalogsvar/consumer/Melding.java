package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

public class Melding implements Serializable {

    public final String id, traadId;
    public final Meldingstype meldingstype;
    public final DateTime opprettetDato;
    public final String fritekst;
    public String tema;
    public DateTime lestDato;
    public boolean lest;
    public Status status;

    public Melding(String id, String traadId, Meldingstype meldingstype, DateTime opprettetDato, String fritekst) {
        this.id = id;
        this.traadId = traadId;
        this.meldingstype = meldingstype;
        this.opprettetDato = opprettetDato;
        this.fritekst = fritekst;
    }

    public static final Transformer<Melding, DateTime> OPPRETTET_DATO = new Transformer<Melding, DateTime>() {
        @Override
        public DateTime transform(Melding melding) {
            return melding.opprettetDato;
        }
    };

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
