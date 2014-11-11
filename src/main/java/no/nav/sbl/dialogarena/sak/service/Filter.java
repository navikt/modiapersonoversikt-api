package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.comparators.OmvendtKronologiskBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.erAvsluttet;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus;
import static org.slf4j.LoggerFactory.getLogger;

public class Filter {

    public static final String OPPRETTET = "opprettet";
    public static final String AVBRUTT = "avbrutt";
    public static final String AVSLUTTET = "avsluttet";

    public final static String DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001";
    public final static String SEND_SOKNAD_KVITTERINGSTYPE = "ae0002";
    public final static String ULOVLIG_PREFIX = "17";

    @Inject
    private CmsContentRetriever cms;
    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    private final static Logger log = getLogger(Filter.class);

    public List<GenerellBehandling> filtrerBehandlinger(List<GenerellBehandling> behandlinger) {
        lovligeBehandlingstyper = asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ArrayList<GenerellBehandling> allebehandlinger = new ArrayList<>();
        allebehandlinger.addAll(on(behandlinger).filter(ER_AVSLUTTET_KVITTERING).collect());
        allebehandlinger.addAll(on(behandlinger).filter(HAR_LOVLIG_BEHANDLINGSTYPE).collect());
        return on(allebehandlinger)
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .collect(new OmvendtKronologiskBehandlingComparator());
    }

    public List<WSSak> filtrerSaker(List<WSSak> saker) {
        ulovligeSakstema = asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        lovligeBehandlingstyper = asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        return on(saker)
                .filter(HAR_LOVLIG_PREFIX_PAA_MINST_EN_BEHANDLING)
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_LOVLIG_STATUS_PAA_MINST_EN_BEHANDLING)
                .filter(HAR_LOVLIGE_BEHANDLINGSTYPER_ELLER_KVITTERINGER).collect();
    }

    public static boolean erKvitteringstype(String type) {
        return SEND_SOKNAD_KVITTERINGSTYPE.equals(type) || DOKUMENTINNSENDING_KVITTERINGSTYPE.equals(type);
    }

    private static final Predicate<? super WSSak> HAR_LOVLIG_STATUS_PAA_MINST_EN_BEHANDLING = new Predicate<WSSak>() {
        @Override
        public boolean evaluate(WSSak wsSak) {
            for (WSBehandlingskjede kjede : wsSak.getBehandlingskjede()) {
                if (kjede.getSisteBehandlingsstatus().getValue() != null &&
                        (kjede.getSisteBehandlingsstatus().getValue().equals(OPPRETTET) && !erKvitteringstype(kjede.getSisteBehandlingstype().getValue())
                                || kjede.getSisteBehandlingsstatus().getValue().equals(AVSLUTTET))) {
                    return true;
                }
            }
            return false;
        }
    };

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_BEHANDLINGSSTATUS = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling record) {
            BehandlingsStatus status = record.behandlingsStatus;
            if (status.equals(BehandlingsStatus.AVSLUTTET) || status.equals(BehandlingsStatus.OPPRETTET)) {
                return true;
            }
            return false;
        }
    };

    private static final Predicate<GenerellBehandling> ER_AVSLUTTET_KVITTERING = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling generellBehandling) {
            if (erKvitteringstype(generellBehandling.behandlingsType) && BehandlingsStatus.AVSLUTTET.equals(generellBehandling.behandlingsStatus)) {
                return true;
            }
            return false;
        }
    };

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_PREFIX = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling behandling) {
            return !ULOVLIG_PREFIX.equals(behandling.prefix);
        }
    };

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_BEHANDLINGSTYPE = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling generellBehandling) {
            return generellBehandling.behandlingsType != null && lovligeBehandlingstyper.contains(generellBehandling.behandlingsType);
        }
    };

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = new Predicate<WSSak>() {
        @Override
        public boolean evaluate(WSSak wsSak) {
            String sakstema = wsSak.getSakstema().getValue();
            boolean erLovlig = !ulovligeSakstema.contains(sakstema);
            if (!erLovlig) {
                log.info(format("Filtrerer bort sakstema %s", sakstema));
            }
            return erLovlig;
        }
    };

    private static final Predicate<WSSak> HAR_LOVLIGE_BEHANDLINGSTYPER_ELLER_KVITTERINGER = new Predicate<WSSak>() {
        @Override
        public boolean evaluate(WSSak wsSak) {
            for (WSBehandlingskjede kjede : wsSak.getBehandlingskjede()) {
                String type = kjede.getSisteBehandlingstype().getValue();
                if ((erKvitteringstype(type) && erAvsluttet(kjede)) || lovligeBehandlingstyper.contains(type)) {
                    return true;
                }
            }
            return false;
        }
    };

    private static final Predicate<? super WSSak> HAR_LOVLIG_PREFIX_PAA_MINST_EN_BEHANDLING = new Predicate<WSSak>() {
        @Override
        public boolean evaluate(WSSak wsSak) {
            for (WSBehandlingskjede kjede : wsSak.getBehandlingskjede()) {
                if (!kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX)) {
                    return true;
                }
            }
            return false;
        }
    };

    private static final Predicate<WSSak> HAR_BEHANDLINGER = new Predicate<WSSak>() {
        @Override
        public boolean evaluate(WSSak wsSak) {
            boolean harBehandlinger = !wsSak.getBehandlingskjede().isEmpty();
            if (!harBehandlinger) {
                log.info(format("Filtrerer bort sak uten behandlinger. Sakstema var %s", wsSak.getSakstema().getValue()));
            }
            return harBehandlinger;
        }
    };

}
