package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.AKTOER;

public class FodselnummerAktorService {

    private static final Logger logger = LoggerFactory.getLogger(FodselnummerAktorService.class);

    @Inject
    private AktoerPortType aktoerPortType;

    public String hentAktorIdForFnr(String fodselsnummer) {
        try {
            HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
            request.setIdent(fodselsnummer);
            return aktoerPortType.hentAktoerIdForIdent(request).getAktoerId();
        } catch (RuntimeException e) {
            logger.error("Det skjedde en uventet feil mot Aktoerservice", e);
            throw new FeilendeBaksystemException(AKTOER);
        }
        catch (Exception e) {
            throw new SystemException("Feil ved henting av aktorId for fnr: " + fodselsnummer, e);
        }
    }
}
