package no.nav.modiapersonoversikt.service.ansattservice.domain;

import java.io.Serializable;

public record Ansatt(String fornavn, String etternavn, String ident) implements Serializable {
}
