package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;

public class SakOgBehandlingTransformers {

    public static final Transformer<List<WSBehandlingskjede>, List<GenerellBehandling>> BEHANDLINGSKJEDER_TIL_BEHANDLINGER =
            new Transformer<List<WSBehandlingskjede>, List<GenerellBehandling>>() {
                @Override
                public List<GenerellBehandling> transform(List<WSBehandlingskjede> wsBehandlingskjeder) {
                    List<GenerellBehandling> behandlinger = wsBehandlingskjeder.stream().map(BEHANDLINGSKJEDE_TIL_BEHANDLING::transform).collect(Collectors.toList());
                    return behandlinger;
                }
            };

    public static final Transformer<WSBehandlingskjede, GenerellBehandling> BEHANDLINGSKJEDE_TIL_BEHANDLING =
            wsBehandlingskjede -> {
                GenerellBehandling generellBehandling = new GenerellBehandling()
                        .withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                        .withOpprettetDato(wsBehandlingskjede.getStart())
                        .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue())
                        .withPrefix(wsBehandlingskjede.getSisteBehandlingREF().substring(0, 2))
                        .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede));
                WSBehandlingstemaer behandlingstema = wsBehandlingskjede.getBehandlingstema();
                if (behandlingstema != null) {
                    generellBehandling = generellBehandling.withBehandlingsTema(behandlingstema.getValue());
                }
                return generellBehandling;
            };

    public static final Transformer<WSSak, List<String>> BEHANDLINGSIDER_FRA_SAK = wsSak -> {
        List<String> behandlingsIder = new ArrayList<>();
        for (WSBehandlingskjede wsBehandlingskjede : wsSak.getBehandlingskjede()) {
            behandlingsIder.addAll(wsBehandlingskjede.getBehandlingsListeRef());
        }
        return behandlingsIder;
    };

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return optional(wsSak.getBehandlingskjede()).isSome() && !wsSak.getBehandlingskjede().isEmpty();
    }

    public static GenerellBehandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVSLUTTET)) {
                return AVSLUTTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.OPPRETTET)) {
                return OPPRETTET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVBRUTT)) {
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
        return Filter.AVSLUTTET.equals(kjede.getSisteBehandlingsstatus().getValue());
    }

    public static Transformer<WSSak, Tema> temaVMTransformer(final FilterImpl filter, final BulletproofKodeverkService kodeverk) {
        return new Transformer<WSSak, Tema>() {

            @Override
            public Tema transform(WSSak wsSak) {
                String temakode = wsSak.getSakstema().getValue();
                Tema tema = new Tema(temakode).withTemanavn(kodeverk.getTemanavnForTemakode(temakode, ARKIVTEMA));
                return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak)) : tema;
            }

            private DateTime hentSistOppdaterteLovligeBehandling(WSSak wsSak) {
                List<GenerellBehandling> behandlinger = on(wsSak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TIL_BEHANDLING).collect();
                List<GenerellBehandling> filtrerteBehandlinger = filter.filtrerBehandlinger(behandlinger);
                List<GenerellBehandling> sorterteFiltrerteBehandlinger = on(filtrerteBehandlinger).collect(new OmvendtKronologiskBehandlingComparator());
                GenerellBehandling forsteBehandling = on(sorterteFiltrerteBehandlinger).head().getOrElse(null);
                return (forsteBehandling != null) ? forsteBehandling.behandlingDato : null;
            }
        };
    }
}
