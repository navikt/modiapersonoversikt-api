package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;

import javax.inject.Inject;

public class FodselnummerAktorService {

    @Inject
    private AktoerPortType aktoerPortType;

    public String hentAktorIdForFnr(String fodselsnummer) {
        try {
            HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
            request.setIdent(fodselsnummer);
            return aktoerPortType.hentAktoerIdForIdent(request).getAktoerId();
        } catch (Exception e) {
            throw new SystemException("Feil ved henting av aktorId for fnr: " + fodselsnummer, e);
        }
    }
}
