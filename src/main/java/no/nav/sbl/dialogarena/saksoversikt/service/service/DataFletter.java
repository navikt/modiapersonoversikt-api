package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.transformTilGenerellBehandling;

public class DataFletter {

    public List<Behandling> flettDataFraBaksystemer(WSSak wsSak, List<Behandling> kvitteringerFraHenvendelse) {
        final List<WSBehandlingskjede> behandlingskjeder = wsSak.getBehandlingskjede();
        List<Behandling> behandlingerUtenKvitteringskobling = finnBehandlingerUtenKvitteringskobling(kvitteringerFraHenvendelse, behandlingskjeder);
        List<Behandling> behandlingerMedKvitteringskobling = finnBehandlingerMedKvitteringskobling(kvitteringerFraHenvendelse, behandlingskjeder);

        List<Behandling> beriketeKvitteringer = berikKvitteringer(behandlingerMedKvitteringskobling, kvitteringerFraHenvendelse);

        return slaaSammenbehandlinger(behandlingerUtenKvitteringskobling, beriketeKvitteringer);
    }

    private List<Behandling> finnBehandlingerMedKvitteringskobling(List<Behandling> kvitteringerFraHenvendelse, List<WSBehandlingskjede> behandlingskjeder) {
        List<WSBehandlingskjede> behandlingskjederMedKvitteringskobling = behandlingskjeder.stream()
                .filter(finnesKvittering(kvitteringerFraHenvendelse))
                .collect(toList());
        return hentBehandlingerFraBehandlingskjeder(behandlingskjederMedKvitteringskobling);
    }

    private List<Behandling> finnBehandlingerUtenKvitteringskobling(List<Behandling> kvitteringerFraHenvendelse, List<WSBehandlingskjede> behandlingskjeder) {
        List<WSBehandlingskjede> behandlingskjederUtenKvitteringskobling = behandlingskjeder.stream()
                .filter((finnesKvittering(kvitteringerFraHenvendelse).negate()))
                .filter((harKvitteringsBehandlingstype().negate()))
                .collect(toList());
        return hentBehandlingerFraBehandlingskjeder(behandlingskjederUtenKvitteringskobling);
    }

    private static Predicate<WSBehandlingskjede> harKvitteringsBehandlingstype() {
        return wsBehandlingskjede -> Filter.erKvitteringstype(wsBehandlingskjede.getSisteBehandlingstype().getValue());
    }

    private List<Behandling> berikKvitteringer(List<Behandling> behandlingerMedKvitteringskobling, List<Behandling> kvitteringerFraHenvendelse) {
        List<Behandling> beriketeKvitteringer = new ArrayList<>();
        for (Behandling behandlingMedKvitteringsKobling : behandlingerMedKvitteringskobling) {
            Behandling kvittering = finnKvitteringMedId(behandlingMedKvitteringsKobling.getBehandlingsId(), kvitteringerFraHenvendelse);
            kvittering = kvittering
                    .withBehandlingsType(behandlingMedKvitteringsKobling.getBehandlingsType())
                    .withBehandlingKvittering(Behandling.BehandlingsType.KVITTERING)
                    .withBehandlingsTema(behandlingMedKvitteringsKobling.getBehandlingstema());
            beriketeKvitteringer.add(kvittering);
        }
        return beriketeKvitteringer;
    }

    private Behandling finnKvitteringMedId(String behandlingsId, List<Behandling> kvitteringerFraHenvendelse) {
        for (Behandling kvittering : kvitteringerFraHenvendelse) {
            if (kvittering.getBehandlingsId().equals(behandlingsId)) {
                return kvittering;
            }
        }
        throw new ApplicationException("Fant ikke kvittering i Henvendelse");
    }

    private Predicate<WSBehandlingskjede> finnesKvittering(List<Behandling> kvitteringer) {
        return wsBehandlingskjede -> kvitteringer.stream()
                .filter(kvittering -> kvittering.getBehandlingsId().equals(wsBehandlingskjede.getSisteBehandlingREF()))
                .findAny()
                .isPresent();
    }

    public static List<Behandling> hentBehandlingerFraBehandlingskjeder(List<WSBehandlingskjede> behandlingskjedeListe) {
        return behandlingskjedeListe.stream()
                .map(wsBehandlingskjede ->  transformTilGenerellBehandling(wsBehandlingskjede))
                .collect(toList());
    }


    private final List<Behandling> slaaSammenbehandlinger(List<Behandling>... behandlingslister) {
        List<Behandling> sammenslaatteBehandlinger = new ArrayList<>();
        asList(behandlingslister).forEach(behandlinger -> sammenslaatteBehandlinger.addAll(behandlinger));
        return sammenslaatteBehandlinger;
    }
}
