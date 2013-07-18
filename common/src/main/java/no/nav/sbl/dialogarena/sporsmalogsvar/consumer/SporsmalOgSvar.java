package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import java.io.Serializable;

public class SporsmalOgSvar implements Serializable {

    public Melding sporsmal, svar;

    public SporsmalOgSvar withSporsmal(Melding sporsmal) {
        this.sporsmal = sporsmal;
        return this;
    }

    public SporsmalOgSvar withSvar(Melding svar) {
        this.svar = svar;
        return this;
    }
}
