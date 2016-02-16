package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj;

import org.joda.time.DateTime;

import java.util.Optional;

import static java.util.Optional.empty;

public class Sak {
    private String temakode;
    private String saksId;
    private Optional<DateTime> avsluttet = empty();
    private String fagsystem;
    private Baksystem baksystem;

    public Optional<DateTime> getAvsluttet() {
        return avsluttet;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public Baksystem getBaksystem() {
        return baksystem;
    }

    public String getTemakode() {
        return temakode;
    }

    public String getSaksId() {
        return saksId;
    }

    public String temakode() {
        return this.temakode;
    }

    public String saksId() {
        return this.saksId;
    }

    public Optional<DateTime> avsluttet() {
        return this.avsluttet;
    }

    public String fagsystem() {
        return this.fagsystem;
    }

    public Baksystem baksystem() {
        return this.baksystem;
    }

    public Sak withTemakode(final String temakode) {
        this.temakode = temakode;
        return this;
    }

    public Sak withSaksId(final String saksId) {
        this.saksId = saksId;
        return this;
    }

    public Sak withAvsluttet(final Optional<DateTime> avsluttet) {
        this.avsluttet = avsluttet;
        return this;
    }

    public Sak withFagsystem(final String fagsystem) {
        this.fagsystem = fagsystem;
        return this;
    }

    public Sak withBaksystem(final Baksystem baksystem) {
        this.baksystem = baksystem;
        return this;
    }
}

