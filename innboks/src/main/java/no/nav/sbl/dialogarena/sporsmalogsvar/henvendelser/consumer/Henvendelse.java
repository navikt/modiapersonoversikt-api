package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer;

import java.io.Serializable;
import org.joda.time.DateTime;

public class Henvendelse implements Serializable {

    public Henvendelse(String id, Henvendelsetype type, String traadId) {
        this.id = id;
        this.type = type;
        this.traadId = traadId;
    }
    public final String id, traadId;
    public final Henvendelsetype type;
    public String tema, overskrift, fritekst;
    public DateTime opprettet, lestDato;
    private boolean lest;

    public void markerSomLest() {
        setLest(true);
        lestDato = DateTime.now();
    }

    public void setLest(boolean lest) {
        this.lest = lest;
    }

    public boolean erLest() {
        return lest;
    }
}
