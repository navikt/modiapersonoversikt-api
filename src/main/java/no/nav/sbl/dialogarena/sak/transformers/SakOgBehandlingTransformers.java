package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.domain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.sak.domain.lamell.GenerellBehandling.BehandlingsStatus.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;

public class SakOgBehandlingTransformers {

    public static final GenerellBehandling behandlingskjedeTilBehandling(WSBehandlingskjede WsBehandlingskjede) {
        GenerellBehandling generellBehandling = new GenerellBehandling()
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

    public static final Transformer<WSSak, List<String>> BEHANDLINGSIDER_FRA_SAK = wsSak -> {
        List<String> behandlingsIder = new ArrayList<>();
        wsSak.getBehandlingskjede().stream()
                .forEach(wsBehandlingskjede -> behandlingsIder.addAll(wsBehandlingskjede.getBehandlingsListeRef()));

        return behandlingsIder;
    };

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return of(wsSak.getBehandlingskjede()).isPresent() && !wsSak.getBehandlingskjede().isEmpty();
    }

    public static GenerellBehandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.AVSLUTTET)) {
                return AVSLUTTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.OPPRETTET)) {
                return OPPRETTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.AVBRUTT)) {
                return AVBRUTT;
            } else {
                throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
            }
        }
        throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
    }

    public static DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
        return harSattSluttDato(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
    }

    private static boolean harSattSluttDato(WSBehandlingskjede wsBehandlingskjede) {
        return wsBehandlingskjede.getSlutt() != null;
    }

    public static boolean erAvsluttet(WSBehandlingskjede kjede) {
        if (kjede.getSisteBehandlingsstatus() == null) {
            return false;
        }
        return no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.AVSLUTTET.equals(kjede.getSisteBehandlingsstatus().getValue());
    }

    public static Tema temaVMTransformer(WSSak wsSak, BulletproofKodeverkService bulletproofKodeverkService, Filter filter) {
        String temakode = wsSak.getSakstema().getValue();
        Tema tema = new Tema(temakode).withTemanavn(bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA));
        return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak, filter)) : tema;
    }

    private static DateTime hentSistOppdaterteLovligeBehandling(WSSak wsSak, Filter filter) {
        List<GenerellBehandling> behandlinger = wsSak.getBehandlingskjede().stream()
                .map(wsBehandlingskjede -> behandlingskjedeTilBehandling(wsBehandlingskjede))
                .collect(toList());
        List<GenerellBehandling> filtrerteBehandlinger = filter.filtrerBehandlinger(behandlinger);
        List<GenerellBehandling> sorterteFiltrerteBehandlinger = filtrerteBehandlinger.stream()
                .sorted(new OmvendtKronologiskBehandlingComparator())
                .collect(toList());
        GenerellBehandling forsteBehandling = sorterteFiltrerteBehandlinger.stream().findFirst().orElseGet(null);
        return (forsteBehandling != null) ? forsteBehandling.behandlingDato : null;
    }
}
