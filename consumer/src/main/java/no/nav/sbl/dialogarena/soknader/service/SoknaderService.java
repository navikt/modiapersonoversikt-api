package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.domain.SoknadComparator;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER;

public class SoknaderService {

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    public List<Soknad> getSoknader(String fnr) {
        List<Soknad> soknadList = new ArrayList<>();
        try {
            for (Sak sak : sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(createRequest(fnr)).getSak()) {
                soknadList.addAll(extractSoknaderFromSak(sak));
            }
        } catch (Exception e) {
            throw new ApplicationException("Feil ved henting av søknader", e);
        }
        return on(soknadList).collect(new SoknadComparator());
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private List<Soknad> extractSoknaderFromSak(Sak sak) {
        return on(sak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER).collect();

    }
}
