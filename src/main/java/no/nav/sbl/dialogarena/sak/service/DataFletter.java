package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.sbl.dialogarena.sak.service.Filter.erKvitteringstype;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.KVITTERING;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSIDER_FRA_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSKJEDER_TIL_BEHANDLINGER;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.behandlingsDato;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.temaVMTransformer;

public class DataFletter {

    @Inject
    private Filter filter;

    public Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(List<WSSak> saker, List<WSSoknad> soknader) {
        Map<TemaVM, List<GenerellBehandling>> behandlingerByTema = new HashMap<>();
        for (WSSak sak : filter.filtrerSaker(saker)) {
            TemaVM tema = temaVMTransformer(filter).transform(sak);
            List<GenerellBehandling> filtrertebehandlinger = filter.filtrerBehandlinger(hentSorterteBehandlinger(soknader, sak));
            behandlingerByTema.put(tema, filtrertebehandlinger);
        }
        return behandlingerByTema;
    }

    private List<GenerellBehandling> hentSorterteBehandlinger(List<WSSoknad> soknader, WSSak sak) {
        return on(hentBehandlingerForSak(soknader, sak)).collect(new OmvendtKronologiskBehandlingComparator());
    }

    private List<GenerellBehandling> hentBehandlingerForSak(List<WSSoknad> soknader, WSSak sak) {
        List<WSBehandlingskjede> alleBehandlingskjeder = sak.getBehandlingskjede();
        List<GenerellBehandling> behandlinger = new ArrayList<>();

        List<Kvittering> kvitteringer = hentKvitteringer(soknader, BEHANDLINGSIDER_FRA_SAK.transform(sak));
        behandlinger.addAll(behandlingerSomIkkeErKvitteringer(alleBehandlingskjeder, kvitteringer));

        Map<String, Kvittering> kvitteringerForBehandlingsID = mapKvitteringMedBehandlingsID(kvitteringer);
        Map<String, WSBehandlingskjede> kjederForBehandlingsID = mappedeKjeder(kvitteringerForBehandlingsID.keySet(), finnBehandlingskjederSomHarKvittering(alleBehandlingskjeder, kvitteringer));
        for (String kvitteringsID : kvitteringerForBehandlingsID.keySet()) {
            behandlinger.add(beriketKvittering(kvitteringerForBehandlingsID.get(kvitteringsID), kjederForBehandlingsID.get(kvitteringsID)));
        }

        final String temakode = temaVMTransformer(filter).transform(sak).temakode;
        behandlinger = on(behandlinger).map(new Transformer<GenerellBehandling, GenerellBehandling>() {
            @Override public GenerellBehandling transform(GenerellBehandling generellBehandling) {
                return generellBehandling.withSaksTema(temakode);
            }
        }).collect();

        return behandlinger;
    }

    private Kvittering beriketKvittering(Kvittering kvittering, WSBehandlingskjede wsBehandlingskjede) {
        return (Kvittering) kvittering.withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue());
    }

    private List<GenerellBehandling> behandlingerSomIkkeErKvitteringer(List<WSBehandlingskjede> alleBehandlingskjeder, List<Kvittering> kvitteringer) {
        return BEHANDLINGSKJEDER_TIL_BEHANDLINGER.transform(behandlingskjederUtenKvitteringer(alleBehandlingskjeder, kvitteringer).collect());
    }

    private List<WSBehandlingskjede> finnBehandlingskjederSomHarKvittering(List<WSBehandlingskjede> alleBehandlingskjeder, List<Kvittering> kvitteringer) {
        return on(alleBehandlingskjeder).filter(finnesKvitteringMedSammeBehandlingsid(kvitteringer)).collect();
    }

    private Map<String, Kvittering> mapKvitteringMedBehandlingsID(List<Kvittering> kvitteringer) {
        Map<String, Kvittering> map = new HashMap<>();
        for (Kvittering kvittering : kvitteringer) {
            map.put(kvittering.behandlingsId, kvittering);
        }
        return map;
    }

    private Map<String, WSBehandlingskjede> mappedeKjeder(Set<String> kvitteringsIDer, List<WSBehandlingskjede> behandlingskjederMedKvitteringskobling) {
        Map<String, WSBehandlingskjede> kjederMappetViaID = new HashMap<>();
        for (WSBehandlingskjede behandlingskjede : behandlingskjederMedKvitteringskobling) {
            for (String potensiellKvitteringsID : behandlingskjede.getBehandlingsListeRef()) {
                if (kvitteringsIDer.contains(potensiellKvitteringsID)) {
                    kjederMappetViaID.put(potensiellKvitteringsID, behandlingskjede);
                }
            }
        }
        return kjederMappetViaID;
    }

    private PreparedIterable<WSBehandlingskjede> behandlingskjederUtenKvitteringer(List<WSBehandlingskjede> behandlingskjeder, List<Kvittering> kvitteringerForTema) {
        return on(behandlingskjeder).filter(not(finnesKvitteringMedSammeBehandlingsid(kvitteringerForTema))).filter(not(harKvitteringsBehandlingstype()));
    }

    private Predicate<WSBehandlingskjede> harKvitteringsBehandlingstype() {
        return new Predicate<WSBehandlingskjede>() {
            @Override public boolean evaluate(final WSBehandlingskjede wsBehandlingskjede) {
                return erKvitteringstype(wsBehandlingskjede.getSisteBehandlingstype().getValue());
            }
        };
    }

    private List<Kvittering> hentKvitteringer(List<WSSoknad> soknader, List<String> behandlingsIDer) {
        List<Kvittering> kvitteringer = new ArrayList<>();
        for (String behandlingId : behandlingsIDer) {
            leggTilKvitteringHvisEksisterer(soknader, behandlingId, kvitteringer);
        }
        return kvitteringer;
    }

    private void leggTilKvitteringHvisEksisterer(List<WSSoknad> soknader, String behandlingsId, List<Kvittering> kvitteringer) {
        for (WSSoknad soknad : soknader) {
            if (soknad.getBehandlingsId().equals(behandlingsId)) {
                kvitteringer.add(KVITTERING.transform(soknad));
            }
        }
    }

    private static Predicate<WSBehandlingskjede> finnesKvitteringMedSammeBehandlingsid(final List<Kvittering> kvitteringer) {
        return new Predicate<WSBehandlingskjede>() {
            @Override
            public boolean evaluate(final WSBehandlingskjede wsBehandlingskjede) {
                boolean exists = on(kvitteringer).exists(new Predicate<Kvittering>() {
                    @Override
                    public boolean evaluate(Kvittering kvittering) {
                        return on(wsBehandlingskjede.getBehandlingsListeRef()).head().is(kvittering.behandlingsId);
                    }
                });
                return exists;
            }
        };
    }

}
