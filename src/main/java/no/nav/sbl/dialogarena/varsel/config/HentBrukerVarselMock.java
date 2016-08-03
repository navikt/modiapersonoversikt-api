package no.nav.sbl.dialogarena.varsel.config;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSBrukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse;

import static no.nav.sbl.dialogarena.varsel.config.VarselbestillingMockData.lagVarselbestillingListe;


public class HentBrukerVarselMock implements BrukervarselV1 {

    @Override
    public void ping() {

    }

    @Override
    public WSHentVarselForBrukerResponse hentVarselForBruker(WSHentVarselForBrukerRequest request) throws HentVarselForBrukerUgyldigInput {
        return new WSHentVarselForBrukerResponse()
                .withBrukervarsel(new WSBrukervarsel().withVarselbestillingListe(lagVarselbestillingListe()));
    }
}
