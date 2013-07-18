package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class BesvareSporsmalVM implements Serializable {

    public String behandlingsId, tema, sporsmal, svar;
    public LocalDate opprettet;
    public boolean sensitiv;

    public BesvareSporsmalVM() {}

    public BesvareSporsmalVM(String behandlingsId, String tema, String sporsmal, String svar, LocalDate opprettet, boolean sensitiv) {
        this.behandlingsId = behandlingsId;
        this.tema = tema;
        this.sporsmal = sporsmal;
        this.svar = svar;
        this.opprettet = opprettet;
        this.sensitiv = sensitiv;
    }

}
