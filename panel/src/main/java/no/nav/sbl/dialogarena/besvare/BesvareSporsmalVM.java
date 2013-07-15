package no.nav.sbl.dialogarena.besvare;

import java.io.Serializable;
import org.joda.time.LocalDate;

public class BesvareSporsmalVM implements Serializable {

    public String behandlingsId, tema, sporsmal, svar;
    public LocalDate opprettet;
    public boolean sensitiv;

    public BesvareSporsmalVM() {
        this(null, null, null, null, null, false);
    }

    public BesvareSporsmalVM(String behandlingsId, String tema, String sporsmal, String svar, LocalDate opprettet, boolean sensitiv) {
        this.behandlingsId = behandlingsId;
        this.tema = tema;
        this.sporsmal = sporsmal;
        this.svar = svar;
        this.opprettet = opprettet;
        this.sensitiv = sensitiv;
    }

    public boolean erSynlig() {
        return behandlingsId != null;
    }

}
