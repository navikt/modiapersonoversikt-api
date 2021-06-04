package no.nav.modiapersonoversikt.service.kodeverksmapper;

import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.Optional;

public class KodeverksmapperService {

    private Kodeverksmapper kodeverksmapper;

    @Autowired
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
