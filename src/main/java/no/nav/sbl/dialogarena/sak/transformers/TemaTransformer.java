package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.joda.time.DateTime;

import java.util.List;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.transformTilBehandling;

public class TemaTransformer {

    public static Tema tilTema(WSSak wsSak, BulletproofKodeverkService bulletproofKodeverkService, Filter filter) {
        String temakode = wsSak.getSakstema().getValue();
        Tema tema = new Tema(temakode).withTemanavn(bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA));
        return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak, filter)) : tema;
    }

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return of(wsSak.getBehandlingskjede()).isPresent() && !wsSak.getBehandlingskjede().isEmpty();
    }

    private static DateTime hentSistOppdaterteLovligeBehandling(WSSak wsSak, Filter filter) {
        List<Behandling> behandlinger = wsSak.getBehandlingskjede().stream()
                .map(wsBehandlingskjede -> transformTilBehandling(wsBehandlingskjede))
                .collect(toList());
        List<Behandling> filtrerteBehandlinger = filter.filtrerBehandlinger(behandlinger);
        List<Behandling> sorterteFiltrerteBehandlinger = filtrerteBehandlinger.stream()
                .sorted((o1, o2) -> o2.behandlingDato.compareTo(o1.behandlingDato))
                .collect(toList());

        return sorterteFiltrerteBehandlinger
                .stream()
                .findFirst()
                .map(behandling -> behandling.behandlingDato)
                .orElse(null);
    }
}
