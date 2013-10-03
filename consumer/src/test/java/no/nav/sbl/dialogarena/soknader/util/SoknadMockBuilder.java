package no.nav.sbl.dialogarena.soknader.util;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class SoknadMockBuilder {

    private Soknad soknad;

    public SoknadMockBuilder() {
        soknad = createDefaultSoknad();
    }

    public SoknadMockBuilder withTittel(String tittel) {
        when(soknad.getTittel()).thenReturn(tittel);
        return this;
    }

    public SoknadMockBuilder withSoknadStatus(SoknadStatus soknadStatus) {
        when(soknad.getSoknadStatus()).thenReturn(soknadStatus);
        return this;
    }

    public SoknadMockBuilder withNormertBehandlingsTid(String normertBehandlingsTid) {
        when(soknad.getNormertBehandlingsTid()).thenReturn(normertBehandlingsTid);
        return this;
    }

    public SoknadMockBuilder withUnderBehandlingDato(DateTime underBehandlingDato) {
        when(soknad.getUnderBehandlingDato()).thenReturn(underBehandlingDato);
        return this;
    }

    public SoknadMockBuilder withFerdigDato(DateTime ferdigDato) {
        when(soknad.getFerdigDato()).thenReturn(ferdigDato);
        return this;
    }

    public SoknadMockBuilder withBehandlingskjedeId(String behandlingskjedeId) {
        when(soknad.getBehandlingskjedeId()).thenReturn(behandlingskjedeId);
        return this;
    }

    public SoknadMockBuilder withMottattDato(DateTime mottattDato) {
        when(soknad.getMottattDato()).thenReturn(mottattDato);
        return this;
    }

    public Soknad build() {
        return soknad;
    }

    private Soknad createDefaultSoknad() {
        Soknad mockSoknad = mock(Soknad.class, withSettings().serializable());
        when(mockSoknad.getTittel()).thenReturn("title-MOCK");
        when(mockSoknad.getSoknadStatus()).thenReturn(SoknadStatus.UKJENT);
        when(mockSoknad.getNormertBehandlingsTid()).thenReturn("10 dager-MOCK");
        when(mockSoknad.getUnderBehandlingDato()).thenReturn(null);
        when(mockSoknad.getFerdigDato()).thenReturn(null);
        when(mockSoknad.getMottattDato()).thenReturn(DateTime.now());
        when(mockSoknad.getBehandlingskjedeId()).thenReturn("behandlingsKjedeId-MOCK");
        return mockSoknad;
    }
}
