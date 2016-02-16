package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.KVITTERING;

public class DataFletter {

    public List<Record<? extends GenerellBehandling>> flettDataFraBaksystemer(WSSak wsSak, List<Record<Kvittering>> kvitteringerFraHenvendelse) {
        final List<WSBehandlingskjede> behandlingskjeder = wsSak.getBehandlingskjede();
        List<Record<GenerellBehandling>> behandlingerUtenKvitteringskobling = finnBehandlingerUtenKvitteringskobling(kvitteringerFraHenvendelse, behandlingskjeder);
        List<Record<GenerellBehandling>> behandlingerMedKvitteringskobling = finnBehandlingerMedKvitteringskobling(kvitteringerFraHenvendelse, behandlingskjeder);

        List<Record<Kvittering>> beriketeKvitteringer = berikKvitteringer(behandlingerMedKvitteringskobling, kvitteringerFraHenvendelse);

        return slaaSammenbehandlinger(behandlingerUtenKvitteringskobling, beriketeKvitteringer);
    }

    private List<Record<GenerellBehandling>> finnBehandlingerMedKvitteringskobling(List<Record<Kvittering>> kvitteringerFraHenvendelse, List<WSBehandlingskjede> behandlingskjeder) {
        List<WSBehandlingskjede> behandlingskjederMedKvitteringskobling = on(behandlingskjeder).filter(finnesKvittering(kvitteringerFraHenvendelse)).collect();
        return hentBehandlingerFraBehandlingskjeder(behandlingskjederMedKvitteringskobling);
    }

    private List<Record<GenerellBehandling>> finnBehandlingerUtenKvitteringskobling(List<Record<Kvittering>> kvitteringerFraHenvendelse, List<WSBehandlingskjede> behandlingskjeder) {
        List<WSBehandlingskjede> behandlingskjederUtenKvitteringskobling = on(behandlingskjeder)
                .filter(not(finnesKvittering(kvitteringerFraHenvendelse)))
                .filter(not(harKvitteringsBehandlingstype()))
                .collect();
        return hentBehandlingerFraBehandlingskjeder(behandlingskjederUtenKvitteringskobling);
    }

    private static Predicate<WSBehandlingskjede> harKvitteringsBehandlingstype() {
        return wsBehandlingskjede -> Filter.erKvitteringstype(wsBehandlingskjede.getSisteBehandlingstype().getValue());
    }

    private List<Record<Kvittering>> berikKvitteringer(List<Record<GenerellBehandling>> behandlingerMedKvitteringskobling, List<Record<Kvittering>> kvitteringerFraHenvendelse) {
        List<Record<Kvittering>> beriketeKvitteringer = new ArrayList<>();
        for (Record<GenerellBehandling> behandlingMedKvitteringsKobling : behandlingerMedKvitteringskobling) {
            Record<Kvittering> kvittering = finnKvitteringMedId(behandlingMedKvitteringsKobling.get(GenerellBehandling.BEHANDLINGS_ID), kvitteringerFraHenvendelse);
            kvittering = kvittering.with(GenerellBehandling.BEHANDLINGS_TYPE, behandlingMedKvitteringsKobling.get(GenerellBehandling.BEHANDLINGS_TYPE))
                    .with(GenerellBehandling.BEHANDLINGKVITTERING, KVITTERING)
                    .with(GenerellBehandling.BEHANDLINGSTEMA, behandlingMedKvitteringsKobling.get(GenerellBehandling.BEHANDLINGSTEMA));
            beriketeKvitteringer.add(kvittering);
        }
        return beriketeKvitteringer;
    }

    private Record<Kvittering> finnKvitteringMedId(String behandlingsId, List<Record<Kvittering>> kvitteringerFraHenvendelse) {
        for (Record<Kvittering> kvittering : kvitteringerFraHenvendelse) {
            if (kvittering.get(GenerellBehandling.BEHANDLINGS_ID).equals(behandlingsId)) {
                return kvittering;
            }
        }
        throw new ApplicationException("Fant ikke kvittering i Henvendelse");
    }

    private Predicate<WSBehandlingskjede> finnesKvittering(final List<Record<Kvittering>> kvitteringer) {
        return wsBehandlingskjede -> on(kvitteringer).exists(kvitteringRecord -> {
            return wsBehandlingskjede.getSisteBehandlingREF().equals(kvitteringRecord.get(GenerellBehandling.BEHANDLINGS_ID));
        });
    }

    public static List<Record<GenerellBehandling>> hentBehandlingerFraBehandlingskjeder(List<WSBehandlingskjede> behandlingskjedeListe) {
        return on(behandlingskjedeListe).map(Transformers.BEHANDLINGSKJEDE_TIL_BEHANDLING).collect();
    }

    @SafeVarargs
    private final List<Record<? extends GenerellBehandling>> slaaSammenbehandlinger(List<? extends Record<? extends GenerellBehandling>>... behandlingslister) {
        ArrayList<Record<? extends GenerellBehandling>> sammenslaatteBehandlinger = new ArrayList<>();
        asList(behandlingslister).forEach(sammenslaatteBehandlinger::addAll);
        return sammenslaatteBehandlinger;
    }

}
