package no.nav.sbl.dialogarena.aktorid.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;

import javax.inject.Inject;

public class AktorService {

    @Inject
    private AktoerPortType ws;

    public AktorService() {}

    public String getAktorId(String fodselsnummer) {
        try {
            HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
            request.setIdent(fodselsnummer);
            return ws.hentAktoerIdForIdent(request).getAktoerId();
        } catch (Exception e) {
            throw new SystemException("Feil ved henting av aktorId for fnr: " + fodselsnummer, e);
        }
    }

}
