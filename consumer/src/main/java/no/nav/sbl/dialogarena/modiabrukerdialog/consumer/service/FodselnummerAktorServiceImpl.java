package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FodselnummerAktorServiceImpl implements FodselnummerAktorService {

    private static final Logger logger = LoggerFactory.getLogger(FodselnummerAktorServiceImpl.class);

    @Autowired
    private Aktoer_v2 aktoerPortType;

    public String hentAktorIdForFnr(String fodselsnummer) {
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
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
