package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;

public class SakOgBehandlingFilter {

    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    private final static Logger log = LoggerFactory.getLogger(SakOgBehandlingFilter.class);

    public List<GenerellBehandling> filtrerBehandlinger(List<GenerellBehandling> behandlinger) {
        lovligeBehandlingstyper = asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ArrayList<GenerellBehandling> allebehandlinger = new ArrayList<>();
        allebehandlinger.addAll(on(behandlinger).filter(ER_KVITTERING).collect());
        allebehandlinger.addAll(on(behandlinger).filter(HAR_LOVLIG_BEHANDLINGSTYPE).collect());
        return allebehandlinger;
    }

    public List<WSSak> filtrerSaker(List<WSSak> saker) {
        ulovligeSakstema = asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        return on(saker).filter(HAR_LOVLIG_SAKSTEMA).filter(HAR_BEHANDLINGER).collect();
    }

    private static final Predicate<GenerellBehandling> ER_KVITTERING = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling generellBehandling) {
            return (generellBehandling instanceof Kvittering);
        }
    };

    private static final Predicate<GenerellBehandling> HAR_LOVLIG_BEHANDLINGSTYPE = new Predicate<GenerellBehandling>() {
        @Override
        public boolean evaluate(GenerellBehandling generellBehandling) {
            return generellBehandling.behandlingsType != null && lovligeBehandlingstyper.contains(generellBehandling.behandlingsType);
        }
    };

    private static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            String sakstema = wsSak.getSakstema().getValue();
            boolean erLovlig = !ulovligeSakstema.contains(sakstema);
            if (!erLovlig) {
                log.info(String.format("Filtrerer bort sakstema %s", sakstema));
            }
            return erLovlig;
        }
    };

    private static final Predicate<WSSak> HAR_BEHANDLINGER = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            boolean harBehandlinger = !wsSak.getBehandlingskjede().isEmpty();
            if (!harBehandlinger) {
                log.info(String.format("Filtrerer bort sak uten behandlinger. Sakstema var %s", wsSak.getSakstema().getValue()));
            }
            return harBehandlinger;
        }
    };

}
