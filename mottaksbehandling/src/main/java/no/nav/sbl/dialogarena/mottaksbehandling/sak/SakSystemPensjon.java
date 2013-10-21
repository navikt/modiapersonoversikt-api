package no.nav.sbl.dialogarena.mottaksbehandling.sak;

import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.virksomhet.gjennomforing.sak.pensjon.v1.WSSak;
import no.nav.virksomhet.tjenester.sak.pensjon.meldinger.v1.WSFinnSakListeRequest;
import no.nav.virksomhet.tjenester.sak.pensjon.meldinger.v1.WSFinnSakListeResponse;
import no.nav.virksomhet.tjenester.sak.pensjon.v1.PensjonSak;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class SakSystemPensjon implements Pingable {
    private PensjonSak tjeneste;

    public SakSystemPensjon(PensjonSak tjeneste) {
        this.tjeneste = tjeneste;
    }

    public List<Record<ISak>> saksliste(String fnr) {
        WSFinnSakListeResponse response = tjeneste.finnSakListe(new WSFinnSakListeRequest().withFnr(fnr));
        return on(response.getSakListe()).map(tilSak).collect();
    }

    private static final Transformer<WSSak, Record<ISak>> tilSak = new Transformer<WSSak, Record<ISak>>() {
        @Override
        public Record<ISak> transform(WSSak wsSak) {
            return new Record<ISak>()
                    .with(ISak.sakId, wsSak.getSakId())
                    .with(ISak.status, wsSak.getSaksstatus().getKode())
                    .with(ISak.opprettetDato, wsSak.getSporing().getOpprettetInfo().getOpprettetDato())
                    .with(ISak.generell, false);
        }
    };

    @Override
    public Ping ping() {
        try {
            tjeneste.finnSakListe(new WSFinnSakListeRequest().withFnr(GsakVerdier.TESTFAMILIEN_AREMARK));
        } catch (Exception e) {
            return Ping.feilet("NAV-TJENESTE-PENSJONSAK_ERROR", e);
        }
        return Ping.lyktes("NAV-TJENESTE-PENSJONSAK");
    }
}
