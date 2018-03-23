package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;

import java.util.NoSuchElementException;
import java.util.Optional;

public class DelsvarServiceImpl implements DelsvarService {

    private final HenvendelseUtsendingService henvendelseUtsendingService;

    public DelsvarServiceImpl(HenvendelseUtsendingService henvendelseUtsendingService) {
        this.henvendelseUtsendingService = henvendelseUtsendingService;
    }

    public void svarDelvis(DelsvarRequest request) {
        Melding delsvar = lagDelsvar(hentBrukersSporsmal(request), request);
        try {
            henvendelseUtsendingService.ferdigstillHenvendelse(delsvar, Optional.empty(),
                    Optional.empty(), request.henvendelseId, request.saksbehandlersValgteEnhet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Melding lagDelsvar(Melding brukersSporsmal, DelsvarRequest request) {
        return new Melding()
                .withKanal(Kanal.TEKST.name())
                .withFritekst(new Fritekst(request.svar))
                .withErTilknyttetAnsatt(true)
                .withTemagruppe(brukersSporsmal.temagruppe)
                .withTraadId(request.traadId)
                .withKontorsperretEnhet(brukersSporsmal.kontorsperretEnhet)
                .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                .withFnr(brukersSporsmal.fnrBruker)
                .withNavIdent(request.navIdent)
                .withEksternAktor(request.navIdent)
                .withTilknyttetEnhet(request.saksbehandlersValgteEnhet)
                .withBrukersEnhet(brukersSporsmal.brukersEnhet);
    }

    private Melding hentBrukersSporsmal(DelsvarRequest request) {
        return henvendelseUtsendingService.hentTraad(request.fodselsnummer, request.traadId, request.saksbehandlersValgteEnhet).stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

}
