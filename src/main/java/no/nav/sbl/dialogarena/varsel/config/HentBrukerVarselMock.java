package no.nav.sbl.dialogarena.varsel.config;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;

import static no.nav.sbl.dialogarena.varsel.config.VarselbestillingMockData.lagVarselbestillingListe;


public class HentBrukerVarselMock implements BrukervarselV1 {

    @Override
    public void ping() {

    }

    @Override
    public no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest request) throws HentVarselForBrukerUgyldigInput {
        HentVarselForBrukerResponse response = new HentVarselForBrukerResponse();
        response.setBrukervarsel(new BrukervarselMock(lagVarselbestillingListe()));

        return response;
    }
}
