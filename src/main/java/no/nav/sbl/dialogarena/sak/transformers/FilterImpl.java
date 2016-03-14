package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.erAvsluttet;
import static no.nav.sbl.dialogarena.sak.util.KvitteringstypeUtils.erKvitteringstype;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.AVSLUTTET;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.OPPRETTET;
import static org.slf4j.LoggerFactory.getLogger;

public class FilterImpl {

    private final static Logger log = getLogger(FilterImpl.class);
    private static final String ULOVLIG_PREFIX = "17";

    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    public List<GenerellBehandling> filtrerBehandlinger(List<GenerellBehandling> behandlinger) {
        lovligeBehandlingstyper = asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ArrayList<GenerellBehandling> allebehandlinger = new ArrayList<>();
        allebehandlinger.addAll(behandlinger.stream().filter(ER_AVSLUTTET_KVITTERING).collect(toList()));
        allebehandlinger.addAll(behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE).collect(toList()));
        return allebehandlinger.stream()
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .sorted(new OmvendtKronologiskBehandlingComparator())
                .collect(toList());
    }

    public List<WSSak> filtrerSaker(List<WSSak> saker) {
        ulovligeSakstema = asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        lovligeBehandlingstyper = asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        return saker.stream()
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_MINST_EN_LOVLIG_BEHANDLING)
                .collect(toList());
    }

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_BEHANDLINGSSTATUS =
            generellBehandling -> generellBehandling.behandlingsStatus.equals(BehandlingsStatus.AVSLUTTET) || generellBehandling.behandlingsStatus.equals(BehandlingsStatus.OPPRETTET);

    private static final Predicate<GenerellBehandling> ER_AVSLUTTET_KVITTERING =
            generellBehandling -> erKvitteringstype(generellBehandling.behandlingsType) && BehandlingsStatus.AVSLUTTET.equals(generellBehandling.behandlingsStatus);

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_PREFIX = behandling -> !ULOVLIG_PREFIX.equals(behandling.prefix);

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_BEHANDLINGSTYPE =
            generellBehandling -> generellBehandling.behandlingsType != null && lovligeBehandlingstyper.contains(generellBehandling.behandlingsType);

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA =
            wsSak -> !ulovligeSakstema.contains(wsSak.getSakstema().getValue());

    private static final Predicate<WSSak> HAR_MINST_EN_LOVLIG_BEHANDLING =
            wsSak -> wsSak.getBehandlingskjede().stream()
                    .filter(FilterImpl.HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
                    .filter(FilterImpl.HAR_LOVLIG_STATUS_PAA_BEHANDLING)
                    .filter(FilterImpl.HAR_LOVLIG_PREFIX_PAA_BEHANDLING)
                    .findAny()
                    .isPresent();

    private static Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING =
            kjede -> {
                String type = kjede.getSisteBehandlingstype().getValue();
                return (erKvitteringstype(type) && erAvsluttet(kjede)) || lovligeBehandlingstyper.contains(type);
            };

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING =
            kjede -> kjede.getSisteBehandlingsstatus().getValue() != null &&
                    (kjede.getSisteBehandlingsstatus().getValue().equals(OPPRETTET) &&
                            !erKvitteringstype(kjede.getSisteBehandlingstype().getValue())
                            || kjede.getSisteBehandlingsstatus().getValue().equals(AVSLUTTET));

    private static final Predicate<WSBehandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING =
            kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX);

    private static final Predicate<WSSak> HAR_BEHANDLINGER = wsSak -> {
        boolean harBehandlinger = !wsSak.getBehandlingskjede().isEmpty();
        if (!harBehandlinger) {
            log.info(format("Filtrerer bort sak uten behandlinger. Sakstema var %s", wsSak.getSakstema().getValue()));
        }
        return harBehandlinger;
    };
}
