package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.commons.collections15.Predicate;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class SakOgBehandlingFilter {
    @Inject
    private CmsContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    public static final Predicate<WSSak> HAR_LOVLIG_SAKSTEMA = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            String sakstema = wsSak.getSakstema().getValue();
            return !ulovligeSakstema.contains(sakstema);
        }
    };

    public static final Predicate<WSSak> HAR_BEHANDLINGER = new Predicate<WSSak>() {
        @Override public boolean evaluate(WSSak wsSak) {
            return !wsSak.getBehandlingskjede().isEmpty();
        }
    };

    public static final Predicate<WSBehandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE = new Predicate<WSBehandlingskjede>() {
        @Override public boolean evaluate(WSBehandlingskjede behandlingskjede) {
            String behandlingstype = behandlingskjede.getSisteBehandlingstype().getValue();
            return lovligeBehandlingstyper.contains(behandlingstype);
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
