package no.nav.sbl.dialogarena.mottaksbehandling.sak;

import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class SakSystemGsak implements Pingable {
    private Sak sak;

    public SakSystemGsak(Sak sak) {
        this.sak = sak;
    }

    public List<Record<ISak>> saksliste(String bruker, String... tema) {
        WSFinnGenerellSakListeRequest hentSakslisteForesporsel = new WSFinnGenerellSakListeRequest()
                .withBrukerId(bruker)
                .withFagomradeKodeListe(tema);
        WSFinnGenerellSakListeResponse hentetSaksliste = sak.finnGenerellSakListe(hentSakslisteForesporsel);
        List<WSGenerellSak> saksliste = hentetSaksliste.getSakListe();

        return on(saksliste).map(TIL_SAK).collect();
    }

    private static final Transformer<WSGenerellSak, Record<ISak>> TIL_SAK = new Transformer<WSGenerellSak, Record<ISak>>() {
        @Override
        public Record<ISak> transform(WSGenerellSak wsSak) {
            return new Record<ISak>()
                    .with(ISak.sakId, wsSak.getSakId())
                    .with(ISak.status, wsSak.getStatusKode())
                    .with(ISak.opprettetDato, wsSak.getEndringsinfo().getOpprettetDato())
                    .with(ISak.generell, wsSak.getFagsystemKode().equals("GSAK"));
        }
    };

    @Override
    public Ping ping() {
        try {
            sak.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId("1"));
        } catch (Exception e) {
            return Ping.feilet("NAV-TJENESTE-SAK_ERROR", e);
        }
        return Ping.lyktes("NAV-TJENESTE-SAK_OK");
    }
}
