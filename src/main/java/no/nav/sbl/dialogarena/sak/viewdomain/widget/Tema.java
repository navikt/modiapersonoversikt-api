package no.nav.sbl.dialogarena.sak.viewdomain.widget;

import java.time.LocalDate;

public class Tema {

    public String temakode;
    public String temanavn;
    public LocalDate sistOppdaterteBehandling;

    public Tema(String temakode, String temanavn) {
        this.temakode = temakode;
        this.temanavn = temanavn;
    }
}
