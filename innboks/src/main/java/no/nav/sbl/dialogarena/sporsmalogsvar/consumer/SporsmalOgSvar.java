package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import java.io.Serializable;

public class SporsmalOgSvar implements Serializable {

    public final Melding sporsmal, svar;

    public SporsmalOgSvar(Melding sporsmal, Melding svar) {
        this.sporsmal = sporsmal;
        this.svar = svar;
    }

}
