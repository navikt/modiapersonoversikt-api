package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class SakOgBehandlingFilter {
    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    private final static Logger log = LoggerFactory.getLogger(SakOgBehandlingFilter.class);

    public static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            String sakstema = wsSak.getSakstema().getValue();
            boolean erLovlig = !ulovligeSakstema.contains(sakstema);
            if (!erLovlig) {
                log.info(String.format("Filtrerer bort sakstema %s", sakstema));
            }
            return erLovlig;
        }
    };

    public static final Predicate<WSSak> HAR_BEHANDLINGER = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            boolean harBehandlinger = !wsSak.getBehandlingskjede().isEmpty();
            if (!harBehandlinger) {
                log.info(String.format("Filtrerer bort sak uten behandlinger. Sakstema var %s", wsSak.getSakstema().getValue()));
            }
            return harBehandlinger;
        }
    };

    public static final Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE = new Predicate<WSBehandlingskjede>() {
        @Override public boolean evaluate(WSBehandlingskjede behandlingskjede) {
            String behandlingstype = behandlingskjede.getSisteBehandlingstype().getValue();
            boolean erLovlig = lovligeBehandlingstyper.contains(behandlingstype);
            if (!erLovlig) {
                log.info(String.format("Filtrerer bort behandlingstype %s", behandlingstype));
            }
            return erLovlig;
        }
    };

    public List<WSSak> filtrer(List<WSSak> saker) {
        ulovligeSakstema = Arrays.asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));

        saker = on(saker).filter(HAR_LOVLIG_SAKSTEMA).collect();

        for (WSSak sak : saker) {
            List<WSBehandlingskjede> behandlingskjeder = on(sak.getBehandlingskjede()).filter(HAR_LOVLIG_BEHANDLINGSTYPE).collect();
            sak.getBehandlingskjede().clear();
            sak.withBehandlingskjede(behandlingskjeder);
        }

        saker = on(saker).filter(HAR_BEHANDLINGER).collect();

        return saker;
    }
}
