package no.nav.sbl.dialogarena.besvare;

import java.io.Serializable;

public class SporsmalOgSvar implements Serializable {

    String behandlingsId, tema, sporsmal, svar;

    public SporsmalOgSvar(String behandlingsId, String tema, String sporsmal, String svar) {
        this.behandlingsId = behandlingsId;
        this.tema = tema;
        this.sporsmal = sporsmal;
        this.svar = svar;
    }

}
