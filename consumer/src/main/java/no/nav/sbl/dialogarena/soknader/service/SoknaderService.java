package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.domain.SoknadComparator;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER;

public class SoknaderService {

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;
    private SoknadComparator soknadComparator = new SoknadComparator();

    public List<Soknad> hentSoknader(String aktorId) {
        List<Soknad> soknadList = new ArrayList<>();
        try {
            for (WSSak sak : sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(createRequest(aktorId)).getSak()) {
                soknadList.addAll(extractSoknaderFromSak(sak));
            }
        } catch (Exception e) {
            throw new ApplicationException("Feil ved henting av søknader", e);
        }
        return on(soknadList)
                .filter(where(Soknad.SOKNAD_SOKNAD_STATUS_TRANSFORMER,not(equalTo(Soknad.SoknadStatus.GAMMEL_FERDIG))))
                .collect(soknadComparator);
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private List<Soknad> extractSoknaderFromSak(WSSak sak) {
        return on(sak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER).collect();

    }
}
