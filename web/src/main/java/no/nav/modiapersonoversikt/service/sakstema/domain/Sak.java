package no.nav.modiapersonoversikt.service.sakstema.domain;

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem;
import java.time.OffsetDateTime;

import java.util.Optional;

import static java.util.Optional.empty;

public class Sak {
    private String temakode;
    private Optional<String> saksId;
    private String fagsaksnummer;
    private Optional<OffsetDateTime> avsluttet = empty();
    private String fagsystem;
    private Optional<String> fagsystemNavn;
    private Baksystem baksystem;
    private Optional<OffsetDateTime> opprettetDato = empty();

    public Optional<OffsetDateTime> getAvsluttet() {
        return avsluttet;
    }

    public Optional<OffsetDateTime> getOpprettetDato() {
        return opprettetDato;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public Optional<String> getFagsystemNavn() {
        return fagsystemNavn;
    }

    public Baksystem getBaksystem() {
        return baksystem;
    }

    public String getTemakode() {
        return temakode;
    }

    public String temakode() {
        return this.temakode;
    }

    public String getFagsaksnummer() {
        return fagsaksnummer;
    }

    public Optional<OffsetDateTime> avsluttet() {
        return this.avsluttet;
    }

    public Baksystem baksystem() {
        return this.baksystem;
    }

    public Sak withTemakode(final String temakode) {
        this.temakode = temakode;
        return this;
    }

    public Sak withFagsaksnummer (final String fagsaksnummer) {
        this.fagsaksnummer = fagsaksnummer;
        return this;
    }

    public Sak withAvsluttet(final Optional<OffsetDateTime> avsluttet) {
        this.avsluttet = avsluttet;
        return this;
    }

    public Sak withFagsystem(final String fagsystem) {
        this.fagsystem = fagsystem;
        return this;
    }

    public Sak withFagsystemNavn(final Optional<String> fagsystemNavn) {
        this.fagsystemNavn = fagsystemNavn;
        return this;
    }

    public Sak withBaksystem(final Baksystem baksystem) {
        this.baksystem = baksystem;
        return this;
    }

    public Sak withOpprettetDato(final Optional<OffsetDateTime> opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }
}
