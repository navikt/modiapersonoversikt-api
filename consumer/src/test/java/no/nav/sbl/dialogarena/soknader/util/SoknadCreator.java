package no.nav.sbl.dialogarena.soknader.util;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class SoknadCreator {

    private Soknad soknad;

    public SoknadCreator() {
        soknad = createDefaultSoknad();
    }

    public SoknadCreator withTittel(String tittel) {
        when(soknad.getTittel()).thenReturn(tittel);
        return this;
    }

    public SoknadCreator withSoknadStatus(SoknadStatus soknadStatus) {
        when(soknad.getSoknadStatus()).thenReturn(soknadStatus);
        return this;
    }

    public SoknadCreator withNormertBehandlingsTid(String normertBehandlingsTid) {
        when(soknad.getNormertBehandlingsTid()).thenReturn(normertBehandlingsTid);
        return this;
    }

    public SoknadCreator withUnderBehandlingDato(DateTime underBehandlingDato) {
        when(soknad.getUnderBehandlingDato()).thenReturn(underBehandlingDato);
        return this;
    }

    public SoknadCreator withFerdigDato(DateTime ferdigDato) {
        when(soknad.getFerdigDato()).thenReturn(ferdigDato);
        return this;
    }

    public SoknadCreator withBehandlingskjedeId(String behandlingskjedeId) {
        when(soknad.getBehandlingskjedeId()).thenReturn(behandlingskjedeId);
        return this;
    }

    public SoknadCreator withMottattDato(DateTime mottattDato) {
        when(soknad.getMottattDato()).thenReturn(mottattDato);
        return this;
    }

    public Soknad build() {
        return soknad;
    }

    private Soknad createDefaultSoknad() {
        Soknad soknad = mock(Soknad.class, withSettings().serializable());
        when(soknad.getTittel()).thenReturn("title-MOCK");
        when(soknad.getSoknadStatus()).thenReturn(SoknadStatus.UKJENT);
        when(soknad.getNormertBehandlingsTid()).thenReturn("10 dager-MOCK");
        when(soknad.getUnderBehandlingDato()).thenReturn(null);
        when(soknad.getFerdigDato()).thenReturn(null);
        when(soknad.getMottattDato()).thenReturn(DateTime.now());
        when(soknad.getBehandlingskjedeId()).thenReturn("behandlingsKjedeId-MOCK");
        return soknad;
    }
}
