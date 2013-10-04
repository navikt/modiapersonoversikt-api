package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.domain.SoknadComparator;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.transformToSoeknad;

public class SoknaderService {

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    public List<Soknad> getSoknader(String fnr) {
        List<Soknad> soknadList = new ArrayList<>();
        FinnSakOgBehandlingskjedeListeResponse response = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(createRequest(fnr));
        for (Sak sak : response.getSak()) {
            soknadList.addAll(convertSakToSoknader(sak));
        }
        sort(soknadList, new SoknadComparator());
        return soknadList;
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private List<Soknad> convertSakToSoknader(Sak sak) {
        List<Soknad> soknadList = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
            soknadList.add(transformToSoeknad(behandlingskjede));
        }
        return soknadList;
    }
}
