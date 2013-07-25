package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.io.Serializable;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;

public class SporsmalOgSvarVM implements Serializable {

    MeldingVM sporsmal, svar;

    public SporsmalOgSvarVM() {
        this(new MeldingVM(new Melding()), new MeldingVM(new Melding()));
    }

    public SporsmalOgSvarVM(MeldingVM sporsmal, MeldingVM svar) {
        this.sporsmal = sporsmal;
        this.svar = svar;
    }
}
