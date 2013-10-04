package no.nav.sbl.dialogarena.soknader.util;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class SoknadBuilder {

    private Soknad soknad;

    public SoknadBuilder() {
        soknad = createDefaultSoknad();
    }

    public SoknadBuilder withTittel(String tittel) {
        setInternalState(soknad, "tittel", tittel);
        return this;
    }

    public SoknadBuilder withSoknadStatus(SoknadStatus soknadStatus) {
        setInternalState(soknad, "soknadStatus", soknadStatus);
        return this;
    }

    public SoknadBuilder withNormertBehandlingsTid(String normertBehandlingsTid) {
        setInternalState(soknad, "normertBehandlingsTid", normertBehandlingsTid);
        return this;
    }

    public SoknadBuilder underBehandlingStartDato(DateTime underBehandlingStartDato) {
        setInternalState(soknad, "underBehandlingStartDato", underBehandlingStartDato);
        return this;
    }

    public SoknadBuilder withFerdigDato(DateTime ferdigDato) {
        setInternalState(soknad, "ferdigDato", ferdigDato);
        return this;
    }

    public SoknadBuilder withInnsendtDato(DateTime innsendtDato) {
        setInternalState(soknad, "innsendtDato", innsendtDato);
        return this;
    }

    public Soknad build() {
        return soknad;
    }

    private Soknad createDefaultSoknad() {
        Soknad defaultSoknad = new Soknad();
        setInternalState(defaultSoknad, "innsendtDato", DateTime.now());
        setInternalState(defaultSoknad, "tittel", "tittel-mock");
        setInternalState(defaultSoknad, "soknadStatus", SoknadStatus.MOTTATT);
        setInternalState(defaultSoknad, "underBehandlingStartDato", null);
        setInternalState(defaultSoknad, "ferdigDato", null);
        setInternalState(defaultSoknad, "normertBehandlingsTid", "10 dager-mock");
        return defaultSoknad;
    }
}
