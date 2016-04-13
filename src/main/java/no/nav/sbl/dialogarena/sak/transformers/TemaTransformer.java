package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.joda.time.DateTime;

import java.util.List;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.MED_AVSLUTTETE_KVITTERINGER;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.transformTilBehandling;

public class TemaTransformer {

    public static Tema tilTema(WSSak wsSak, BulletproofKodeverkService bulletproofKodeverkService, Filter filter) {
        String temakode = wsSak.getSakstema().getValue();
        ResultatWrapper temanavnForTemakode = bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA);
        Tema tema = new Tema(temakode).withTemanavn((String) temanavnForTemakode.resultat);
        return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak, filter)) : tema;
    }

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return of(wsSak.getBehandlingskjede()).isPresent() && !wsSak.getBehandlingskjede().isEmpty();
    }

    protected static DateTime hentSistOppdaterteLovligeBehandling(WSSak wsSak, Filter filter) {
        List<Behandling> behandlinger = wsSak.getBehandlingskjede().stream()
                .map(wsBehandlingskjede -> transformTilBehandling(wsBehandlingskjede))
                .collect(toList());
        List<Behandling> filtrerteBehandlinger = filter.filtrerBehandlinger(behandlinger, MED_AVSLUTTETE_KVITTERINGER);
        List<Behandling> sorterteFiltrerteBehandlinger = filtrerteBehandlinger.stream()
                .sorted((o1, o2) -> o2.behandlingDato.compareTo(o1.behandlingDato))
                .collect(toList());

        //Returnerer dato fra 1970 hvis dato ikke finnes
        return sorterteFiltrerteBehandlinger
                .stream()
                .findFirst()
                .map(behandling -> behandling.behandlingDato)
                .orElse(new DateTime(0));
    }
}
