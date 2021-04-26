package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;

import java.io.IOException;
import java.util.Map;

public interface Kodeverksmapper {

    Map<String, String> hentOppgavetype() throws IOException;

    Map<String, Behandling> hentUnderkategori() throws IOException;

    void ping() throws IOException;
}
