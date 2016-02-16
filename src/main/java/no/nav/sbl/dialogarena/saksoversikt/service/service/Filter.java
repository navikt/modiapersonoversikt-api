package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator.OmvendtKronologiskHendelseComparator;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
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
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsStatus;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.KVITTERING;
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

    public List<WSSak> filtrerSaker(List<WSSak> saker) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ulovligeSakstema = Arrays.asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        return saker.stream()
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_MINST_EN_LOVLIG_BEHANDLING).collect(toList());
    }

    public List<Record<GenerellBehandling>> filtrerBehandlinger(List<Record<GenerellBehandling>> behandlinger) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        Stream<Record<GenerellBehandling>> avsluttedeKvitteringer = behandlinger.stream().filter(ER_AVSLUTTET_KVITTERING);
        Stream<Record<GenerellBehandling>> lovligeBehandlinger = behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE);

        return concat(avsluttedeKvitteringer, lovligeBehandlinger)
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .sorted(new OmvendtKronologiskHendelseComparator())
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


    private static final Predicate<Record<GenerellBehandling>> HAR_LOVLIG_BEHANDLINGSSTATUS = record -> {
        BehandlingsStatus status = record.get(GenerellBehandling.BEHANDLING_STATUS);
        return status.equals(BehandlingsStatus.AVSLUTTET) || status.equals(BehandlingsStatus.OPPRETTET);
    };

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING = kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX);

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING = kjede -> {
        if (kjede.getSisteBehandlingsstatus().getValue() != null &&
                ((kjede.getSisteBehandlingsstatus().getValue().equals(OPPRETTET) && !erKvitteringstype(kjede.getSisteBehandlingstype().getValue()))
                        || kjede.getSisteBehandlingsstatus().getValue().equals(AVSLUTTET))) {
            return true;
        }
        return false;
    };

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING = kjede -> {
        String type = kjede.getSisteBehandlingstype().getValue();
        return (erKvitteringstype(type) && erAvsluttet(kjede)) || lovligeBehandlingstyper.contains(type);
    };

    private static Predicate<WSBehandlingskjede> LOVLIG_BEHANDLING = wsBehandlingskjede -> HAR_LOVLIG_STATUS_PAA_BEHANDLING
            .and(HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
            .and(HAR_LOVLIG_PREFIX_PAA_BEHANDLING).test(wsBehandlingskjede);

    private static final Predicate<WSSak> HAR_MINST_EN_LOVLIG_BEHANDLING = wsSak -> wsSak.getBehandlingskjede().stream().anyMatch(LOVLIG_BEHANDLING);

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = wsSak -> {
        String sakstema = wsSak.getSakstema().getValue();
        boolean erLovlig = !ulovligeSakstema.contains(sakstema);
        if (!erLovlig) {
            log.info(String.format("Filtrerer bort sakstema %s", sakstema));
        }
        return erLovlig;
    };

    private static final Predicate<WSSak> HAR_BEHANDLINGER = wsSak -> {
        boolean harBehandlinger = !wsSak.getBehandlingskjede().isEmpty();
        if (!harBehandlinger) {
            log.info(String.format("Filtrerer bort sak uten behandlinger. Sakstema var %s", wsSak.getSakstema().getValue()));
        }
        return harBehandlinger;
    };

    private static final Predicate<Record<GenerellBehandling>> ER_AVSLUTTET_KVITTERING = record -> KVITTERING.equals(record.get(GenerellBehandling.BEHANDLINGKVITTERING)) && record.get(GenerellBehandling.BEHANDLING_STATUS).equals(BehandlingsStatus.AVSLUTTET);

    private static final Predicate<Record<GenerellBehandling>> HAR_LOVLIG_PREFIX = record -> !ULOVLIG_PREFIX.equals(record.get(GenerellBehandling.PREFIX));

    private static final Predicate<Record<GenerellBehandling>> HAR_LOVLIG_BEHANDLINGSTYPE = record -> {
        String behandlingstype = record.get(GenerellBehandling.BEHANDLINGS_TYPE);
        boolean erLovlig = lovligeBehandlingstyper.contains(behandlingstype);
        if (!erLovlig) {
            log.info(String.format("Filtrerer bort behandlingstype %s", behandlingstype));
        }
        return erLovlig;
    };
}
