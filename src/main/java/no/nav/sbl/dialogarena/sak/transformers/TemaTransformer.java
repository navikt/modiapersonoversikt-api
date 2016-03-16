package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import org.joda.time.DateTime;

import java.util.List;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;

public class TemaTransformer {

    public static Tema tilTema(WSSak wsSak, BulletproofKodeverkService bulletproofKodeverkService, Filter filter) {
        String temakode = wsSak.getSakstema().getValue();
        Tema tema = new Tema(temakode).withTemanavn(bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA));
        return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak, filter)) : tema;
    }

    private static Behandling behandlingskjedeTilBehandling(WSBehandlingskjede WsBehandlingskjede) {
        Behandling generellBehandling = new Behandling()
                .withBehandlingsDato(behandlingsDato(WsBehandlingskjede))
                .withOpprettetDato(WsBehandlingskjede.getStart())
                .withBehandlingsType(WsBehandlingskjede.getSisteBehandlingstype().getValue())
                .withPrefix(WsBehandlingskjede.getSisteBehandlingREF().substring(0, 2))
                .withBehandlingStatus(behandlingsStatus(WsBehandlingskjede));
        WSBehandlingstemaer behandlingstema = WsBehandlingskjede.getBehandlingstema();
        if (behandlingstema != null) {
            generellBehandling = generellBehandling.withBehandlingsTema(behandlingstema.getValue());
        }
        return generellBehandling;
    }

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return of(wsSak.getBehandlingskjede()).isPresent() && !wsSak.getBehandlingskjede().isEmpty();
    }

    private static Behandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVSLUTTET)) {
                return Behandling.BehandlingsStatus.AVSLUTTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.OPPRETTET)) {
                return Behandling.BehandlingsStatus.OPPRETTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVBRUTT)) {
                return Behandling.BehandlingsStatus.AVBRUTT;
            } else {
                throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
            }
        }
        throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
    }

    private static DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
        return harSattSluttDato(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
    }

    private static boolean harSattSluttDato(WSBehandlingskjede wsBehandlingskjede) {
        return wsBehandlingskjede.getSlutt() != null;
    }

    private static DateTime hentSistOppdaterteLovligeBehandling(WSSak wsSak, Filter filter) {
        List<Behandling> behandlinger = wsSak.getBehandlingskjede().stream()
                .map(wsBehandlingskjede -> behandlingskjedeTilBehandling(wsBehandlingskjede))
                .collect(toList());
        List<Behandling> filtrerteBehandlinger = filter.filtrerBehandlinger(behandlinger);
        List<Behandling> sorterteFiltrerteBehandlinger = filtrerteBehandlinger.stream()
                .sorted((o1, o2) -> o2.behandlingDato.compareTo(o1.behandlingDato))
                .collect(toList());
        Behandling forsteBehandling = sorterteFiltrerteBehandlinger.stream().findFirst().orElseGet(null);
        return (forsteBehandling != null) ? forsteBehandling.behandlingDato : null;
    }
}
