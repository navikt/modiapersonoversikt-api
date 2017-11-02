package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;

import java.util.NoSuchElementException;
import java.util.Optional;

public class HenvendelseServiceImpl implements HenvendelseService {

    private final HenvendelseUtsendingService henvendelseUtsendingService;

    public HenvendelseServiceImpl(HenvendelseUtsendingService henvendelseUtsendingService) {
        this.henvendelseUtsendingService = henvendelseUtsendingService;
    }

    public void ferdigstill(FerdigstillHenvendelseRequest request) {
        Melding sporsmal = hentBrukersSporsmal(request);
        try {
            henvendelseUtsendingService.ferdigstillHenvendelse(sporsmal, Optional.empty(), Optional.empty(), request.henvendelseId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Melding hentBrukersSporsmal(FerdigstillHenvendelseRequest request) {
        return henvendelseUtsendingService.hentTraad(request.fodselsnummer, request.traadId).stream()
                .findFirst()
                .map(melding -> melding.withKanal(Kanal.TEKST.name()))
                .map(melding -> melding.withNavIdent(request.navIdent))
                .map(melding -> melding.withFritekst(request.svar))
                .map(melding -> melding.withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG))
                .orElseThrow(NoSuchElementException::new);
    }

}
