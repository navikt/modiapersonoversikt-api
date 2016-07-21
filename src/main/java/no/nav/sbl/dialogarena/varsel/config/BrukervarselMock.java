package no.nav.sbl.dialogarena.varsel.config;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;

import java.util.ArrayList;

public class BrukervarselMock extends Brukervarsel {

    public BrukervarselMock() {
        varselbestillingListe = new ArrayList<>();
        varselbestillingListe.add(new VarselbestillingMock());
    }
}