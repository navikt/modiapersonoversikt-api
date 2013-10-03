package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.soknader.domain.Soknad.transformToSoeknad;

public class SoknaderServiceImpl implements SoknaderService {

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @Override
    public List<Soknad> getSoknader(String fnr) {
        List<Soknad> soknadList = new ArrayList<>();
        FinnSakOgBehandlingskjedeListeResponse response = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(createRequest(fnr));
        for (Sak sak : response.getSak()) {
            soknadList.addAll(convertSakToSoknader(sak));
        }
        return soknadList;
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private List<Soknad> convertSakToSoknader(Sak sak) {
        String temaKodeRef = sak.getTema().getKodeRef();
        //        String temaKodeverkRef = sak.getTema().getKodeverksRef();

        //TODO: Gjøre oppslag mot kodeverk, og finne sakstemaet her før implementasjon mot sak og behandling
        String tittel = temaKodeRef;
        List<Soknad> soknadList = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
            soknadList.add(transformToSoeknad(behandlingskjede, tittel));
        }
        return soknadList;
    }
}
