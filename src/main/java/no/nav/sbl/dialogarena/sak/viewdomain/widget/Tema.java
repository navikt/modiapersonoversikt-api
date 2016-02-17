package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import org.joda.time.DateTime;

public class Tema {

    public String temakode;
    public String temanavn;
    public DateTime behandlingsdato;

    public Tema(String temakode) {
        this.temakode = temakode;
    }

    public Tema withSistOppdaterteBehandling(DateTime behandlingsdato) {
        this.behandlingsdato = behandlingsdato;
        return this;
    }

    public Tema withTemanavn(String temanavn) {
        this.temanavn = temanavn;
        return this;
    }
}
