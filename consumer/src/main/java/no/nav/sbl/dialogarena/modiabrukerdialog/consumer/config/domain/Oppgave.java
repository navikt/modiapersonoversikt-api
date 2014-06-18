package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import no.nav.modig.lang.option.Optional;
import org.joda.time.LocalDate;

public class Oppgave {
    private String id, behandlingsid, fodselsnummer, tema;
    private Optional<String> saksbehandlerid, beskrivelse;
    private boolean ferdigstilt;
    private LocalDate aktivFra;
    private int versjon;

    public String getId() {
        return id;
    }

    public String getBehandlingsid() {
        return behandlingsid;
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public String getTema() {
        return tema;
    }

    public Optional<String> getSaksbehandlerid() {
        return saksbehandlerid;
    }

    public Optional<String> getBeskrivelse() {
        return beskrivelse;
    }

    public boolean isFerdigstilt() {
        return ferdigstilt;
    }

    public LocalDate getAktivFra() {
        return aktivFra;
    }

    public int getVersjon() {
        return versjon;
    }

    public Oppgave withId(String id) {
        this.id = id;
        return this;
    }

    public Oppgave withBehandlingsid(String behandlingsid) {
        this.behandlingsid = behandlingsid;
        return this;

    }

    public Oppgave withFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
        return this;

    }

    public Oppgave withTema(String tema) {
        this.tema = tema;
        return this;

    }

    public Oppgave withSaksbehandlerid(Optional<String> saksbehandlerid) {
        this.saksbehandlerid = saksbehandlerid;
        return this;

    }

    public Oppgave withBeskrivelse(Optional<String> beskrivelse) {
        this.beskrivelse = beskrivelse;
        return this;

    }

    public Oppgave withFerdigstilt(boolean ferdigstilt) {
        this.ferdigstilt = ferdigstilt;
        return this;

    }

    public Oppgave withAktivFra(LocalDate aktivFra) {
        this.aktivFra = aktivFra;
        return this;

    }

    public Oppgave withVersjon(int versjon) {
        this.versjon = versjon;
        return this;
    }
}
