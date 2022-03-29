package no.nav.modiapersonoversikt.service.ansattservice.domain;

import java.io.Serializable;

public class Ansatt implements Serializable {
    public final String fornavn, etternavn, ident;

    public Ansatt(String fornavn, String etternavn, String ident) {
        this.fornavn = fornavn;
        this.etternavn = etternavn;
        this.ident = ident;
    }
}
