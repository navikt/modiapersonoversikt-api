package no.nav.sbl.dialogarena.varsel.config;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;

import java.util.List;

public class BrukervarselMock extends Brukervarsel {

    public BrukervarselMock(List<Varselbestilling> varselbestillingsliste) {
        varselbestillingListe = varselbestillingsliste;
    }

}