package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.KVITTERING;
import static org.slf4j.LoggerFactory.getLogger;

public class Filter {
    public static final String OPPRETTET = "opprettet";
    public static final String AVBRUTT = "avbrutt";
    public static final String AVSLUTTET = "avsluttet";
    public final static String DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001";
    public final static String SEND_SOKNAD_KVITTERINGSTYPE = "ae0002";
    public final static String ULOVLIG_PREFIX = "17";
    public static final String BEHANDLINGSTATUS_AVSLUTTET = "avsluttet";

    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    private final static Logger log = getLogger(Filter.class);

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
        Stream<Behandling> avsluttedeKvitteringer = behandlinger.stream().filter(ER_AVSLUTTET_KVITTERING);
        Stream<Behandling> lovligeBehandlinger = behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE);

        return concat(avsluttedeKvitteringer, lovligeBehandlinger)
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .sorted((o1, o2) -> o2.getBehandlingDato().compareTo(o1.getBehandlingDato()))
                .collect(toList());
    }

    public static boolean erAvsluttet(WSBehandlingskjede kjede) {
        boolean erAvsluttet = kjede.getSisteBehandlingsstatus() != null && BEHANDLINGSTATUS_AVSLUTTET.equals(kjede.getSisteBehandlingsstatus().getValue());
        if (erAvsluttet && kjede.getSlutt() == null) {
            log.warn("Inkonsistent data fra sak og behandling: Behandling rapporteres som avsluttet uten at kjede har slutt-tid satt. " +
                    "Behandlingsid: " + kjede.getSisteBehandlingREF() +
                    "Behandlingskjedeid: " + kjede.getBehandlingskjedeId());
        }
        return erAvsluttet;
    }

    public static boolean erKvitteringstype(String type) {
        return type.equals(SEND_SOKNAD_KVITTERINGSTYPE) || type.equals(DOKUMENTINNSENDING_KVITTERINGSTYPE);
    }

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSSTATUS = behandling -> !behandling.getBehandlingsStatus().equals(BehandlingsStatus.AVBRUTT);

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING = kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX);

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING = kjede ->
            kjede.getSisteBehandlingsstatus().getValue() != null &&
            ((kjede.getSisteBehandlingsstatus().getValue().equals(OPPRETTET) && !erKvitteringstype(kjede.getSisteBehandlingstype().getValue()))
                    || kjede.getSisteBehandlingsstatus().getValue().equals(AVSLUTTET));

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING = kjede -> {
        String type = kjede.getSisteBehandlingstype().getValue();
        return (erKvitteringstype(type) && erAvsluttet(kjede)) || lovligeBehandlingstyper.contains(type);
    };

    private static final Predicate<WSBehandlingskjede> LOVLIG_BEHANDLING = wsBehandlingskjede -> HAR_LOVLIG_STATUS_PAA_BEHANDLING
            .and(HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
            .and(HAR_LOVLIG_PREFIX_PAA_BEHANDLING).test(wsBehandlingskjede);

    private static final Predicate<WSSak> HAR_MINST_EN_LOVLIG_BEHANDLING = wsSak -> wsSak.getBehandlingskjede().stream().anyMatch(LOVLIG_BEHANDLING);

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = wsSak -> !ulovligeSakstema.contains(wsSak.getSakstema().getValue());

    private static final Predicate<WSSak> HAR_BEHANDLINGER = wsSak -> !wsSak.getBehandlingskjede().isEmpty();

    private static final Predicate<Behandling> ER_AVSLUTTET_KVITTERING
            = behandling -> KVITTERING.equals(behandling.getBehandlingkvittering())
            && behandling.getBehandlingsStatus().equals(FERDIG_BEHANDLET);

    private static final Predicate<Behandling> HAR_LOVLIG_PREFIX = behandling -> !ULOVLIG_PREFIX.equals(behandling.getPrefix());

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSTYPE = behandling -> lovligeBehandlingstyper.contains(behandling.getBehandlingsType());
}
