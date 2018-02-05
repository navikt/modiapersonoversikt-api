package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class KodeverksmapperService {

    private Kodeverksmapper kodeverksmapper;

    @Inject
    public KodeverksmapperService(Kodeverksmapper kodeverksmapper) {
        this.kodeverksmapper = kodeverksmapper;
    }

    public String mapOppgavetype(String oppgavetype) throws IOException {
        return kodeverksmapper.hentOppgavetype().get(oppgavetype);

    }

    public Optional<Behandling> mapUnderkategori(String underkategori) throws IOException {
        return Optional.ofNullable(kodeverksmapper.hentUnderkategori().get(underkategori));
    }
}
