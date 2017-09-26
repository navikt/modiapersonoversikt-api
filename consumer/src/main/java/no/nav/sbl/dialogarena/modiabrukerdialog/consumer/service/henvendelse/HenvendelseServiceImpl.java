package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class HenvendelseServiceImpl implements HenvendelseService {

    private final HenvendelseUtsendingService henvendelseUtsendingService;

    public HenvendelseServiceImpl(HenvendelseUtsendingService henvendelseUtsendingService) {
        this.henvendelseUtsendingService = henvendelseUtsendingService;
    }

    public void ferdigstill(String fnr, String traadID, String henvendelseId, String innhold) {
        Melding sporsmal = hentBrukersSporsmal(fnr, traadID);
        try {
            henvendelseUtsendingService.ferdigstillHenvendelse(sporsmal, Optional.empty(), Optional.empty(), henvendelseId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Melding hentBrukersSporsmal(String fnr, String traadID) {
        return henvendelseUtsendingService.hentTraad(fnr, traadID).stream()
                .findFirst()
                .map(melding -> melding.withKanal(Meldingstype.SVAR_SKRIFTLIG.name()))
                .map(melding -> melding.withNavIdent(getSubjectHandler().getUid()))
                .orElseThrow(NoSuchElementException::new);
    }

}
