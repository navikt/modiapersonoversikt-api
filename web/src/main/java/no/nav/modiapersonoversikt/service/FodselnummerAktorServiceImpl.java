package no.nav.modiapersonoversikt.service;

import no.nav.modiapersonoversikt.infrastructure.core.exception.SystemException;
import no.nav.modiapersonoversikt.legacy.api.service.FodselnummerAktorService;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FodselnummerAktorServiceImpl implements FodselnummerAktorService {

    private static final Logger logger = LoggerFactory.getLogger(FodselnummerAktorServiceImpl.class);

    @Autowired
    private Aktoer_v2 aktoerPortType;

    @NotNull
    public String hentAktorIdForFnr(@NotNull String fodselsnummer) {
        try {
            WSHentAktoerIdForIdentRequest request = new WSHentAktoerIdForIdentRequest();
            request.setIdent(fodselsnummer);
            return aktoerPortType.hentAktoerIdForIdent(request).getAktoerId();
        } catch (Exception e) {
            logger.error("Det skjedde en uventet feil mot Aktoerservice", e);
            throw new SystemException("Feil ved henting av aktorId for fnr: " + fodselsnummer, e);
        }
    }

    @NotNull
    @Override
    public String hentFnrForAktorId(@NotNull String aktorId) {
        try {
            WSHentIdentForAktoerIdRequest request = new WSHentIdentForAktoerIdRequest();
            request.setAktoerId(aktorId);
            return aktoerPortType.hentIdentForAktoerId(request).getIdent();
        } catch (Exception e) {
            logger.error("Det skjedde en uventet feil mot Aktoerservice", e);
            throw new SystemException("Feil ved henting av fnr for aktorId: " + aktorId, e);
        }
    }
}
