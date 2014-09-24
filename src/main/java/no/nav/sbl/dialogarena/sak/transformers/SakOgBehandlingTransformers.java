package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingFilter;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.OPPRETTET;

public class SakOgBehandlingTransformers {

    public static final Transformer<List<WSBehandlingskjede>, List<GenerellBehandling>> BEHANDLINGSKJEDER_TIL_BEHANDLINGER =
            new Transformer<List<WSBehandlingskjede>, List<GenerellBehandling>>() {
                @Override
                public List<GenerellBehandling> transform(List<WSBehandlingskjede> wsBehandlingskjeder) {
                    List<GenerellBehandling> behandlinger = new ArrayList<>();
                    for (WSBehandlingskjede wsBehandlingskjede : wsBehandlingskjeder) {
                        behandlinger.add(BEHANDLINGSKJEDE_TIL_BEHANDLING.transform(wsBehandlingskjede));
                    }
                    return behandlinger;
                }
            };

    public static final Transformer<WSBehandlingskjede, GenerellBehandling> BEHANDLINGSKJEDE_TIL_BEHANDLING =
            new Transformer<WSBehandlingskjede, GenerellBehandling>() {

                @Override
                public GenerellBehandling transform(WSBehandlingskjede wsBehandlingskjede) {
                    GenerellBehandling generellBehandling = new GenerellBehandling()
                            .withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                            .withOpprettetDato(wsBehandlingskjede.getStart())
                            .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue())
                            .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede));
                    WSBehandlingstemaer behandlingstema = wsBehandlingskjede.getBehandlingstema();
                    if (behandlingstema != null) {
                        generellBehandling = generellBehandling.withBehandlingsTema(behandlingstema.getValue());
                    }
                    return generellBehandling;
                }
            };

    public static final Transformer<WSSak, List<String>> BEHANDLINGSIDER_FRA_SAK = new Transformer<WSSak, List<String>>() {
        @Override
        public List<String> transform(WSSak wsSak) {
            List<String> behandlingsIder = new ArrayList<>();
            for (WSBehandlingskjede wsBehandlingskjede : wsSak.getBehandlingskjede()) {
                behandlingsIder.addAll(wsBehandlingskjede.getBehandlingsListeRef());
            }
            return behandlingsIder;
        }
    };


    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return optional(wsSak.getBehandlingskjede()).isSome() && !wsSak.getBehandlingskjede().isEmpty();
    }

    private static GenerellBehandling hentForsteBehandlingskjede(WSSak wsSak) {
        return on(wsSak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TIL_BEHANDLING).collect(new OmvendtKronologiskBehandlingComparator()).get(0);
    }

    public static GenerellBehandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        return erAvsluttet(wsBehandlingskjede) ? AVSLUTTET : OPPRETTET;
    }

    public static DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
        return erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
    }

    public static boolean erAvsluttet(WSBehandlingskjede wsBehandlingskjede) {
        return wsBehandlingskjede.getSlutt() != null;
    }

    public static Transformer<WSSak, TemaVM> temaVMTransformer(final SakOgBehandlingFilter filter) {
        return new Transformer<WSSak, TemaVM>() {

            @Override
            public TemaVM transform(WSSak wsSak) {
                TemaVM tema = new TemaVM().withTemaKode(wsSak.getSakstema().getValue());
                return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentSistOppdaterteLovligeBehandling(wsSak)) : tema;
            }

            private GenerellBehandling hentSistOppdaterteLovligeBehandling(WSSak wsSak) {
                return on(filter.filtrerBehandlinger(on(wsSak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TIL_BEHANDLING).collect())).collect(new OmvendtKronologiskBehandlingComparator()).get(0);
            }
        };
    }

}
