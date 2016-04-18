package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FilterUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Filter {
    public final static String ULOVLIG_PREFIX = "17";

    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    public synchronized List<WSSak> filtrerSaker(List<WSSak> saker) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ulovligeSakstema = Arrays.asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        return saker.stream()
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_MINST_EN_LOVLIG_BEHANDLING).collect(toList());
    }

    public synchronized List<Behandling> filtrerBehandlinger(List<Behandling> behandlinger) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        Stream<Behandling> lovligeBehandlinger = behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE);

        return lovligeBehandlinger
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .sorted((o1, o2) -> o2.getBehandlingDato().compareTo(o1.getBehandlingDato()))
                .collect(toList());
    }

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSSTATUS = behandling -> !behandling.getBehandlingsStatus().equals(BehandlingsStatus.AVBRUTT);

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING = kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX);

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING = kjede ->
            kjede.getSisteBehandlingsstatus().getValue() != null &&
            ((kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.OPPRETTET) && !FilterUtils.erKvitteringstype(kjede.getSisteBehandlingstype().getValue()))
                    || kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.AVSLUTTET));

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING = kjede -> {
        String type = kjede.getSisteBehandlingstype().getValue();
        return (FilterUtils.erKvitteringstype(type) && FilterUtils.erAvsluttet(kjede)) || lovligeBehandlingstyper.contains(type);
    };

    private static final Predicate<WSBehandlingskjede> LOVLIG_BEHANDLING = wsBehandlingskjede -> HAR_LOVLIG_STATUS_PAA_BEHANDLING
            .and(HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
            .and(HAR_LOVLIG_PREFIX_PAA_BEHANDLING).test(wsBehandlingskjede);

    private static final Predicate<WSSak> HAR_MINST_EN_LOVLIG_BEHANDLING = wsSak -> wsSak.getBehandlingskjede().stream().anyMatch(LOVLIG_BEHANDLING);

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = wsSak -> !ulovligeSakstema.contains(wsSak.getSakstema().getValue());

    private static final Predicate<WSSak> HAR_BEHANDLINGER = wsSak -> !wsSak.getBehandlingskjede().isEmpty();

    private static final Predicate<Behandling> HAR_LOVLIG_PREFIX = behandling -> !ULOVLIG_PREFIX.equals(behandling.getPrefix());

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSTYPE = behandling -> lovligeBehandlingstyper.contains(behandling.getBehandlingsType());
}
