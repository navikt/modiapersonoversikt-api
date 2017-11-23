package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class KodeverksmapperService {

    private static final Logger LOG = LoggerFactory.getLogger(KodeverksmapperService.class);
    private Kodeverksmapper kodeverksmapper;

    @Inject
    public KodeverksmapperService(Kodeverksmapper kodeverksmapper) {
        this.kodeverksmapper = kodeverksmapper;
    }

    public String mapOppgavetype(String oppgavetype) {
        try {
            return kodeverksmapper.hentOppgavetype().get(oppgavetype);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public Optional<Behandling> mapUnderkategori(String underkategori) {
        try {
            return Optional.ofNullable(kodeverksmapper.hentUnderkategori().get(underkategori));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
