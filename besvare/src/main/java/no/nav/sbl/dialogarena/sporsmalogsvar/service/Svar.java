package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import java.io.Serializable;

public class Svar implements Serializable {

    public final String behandlingId, tema, fritekst;

    public final boolean sensitiv;

    public Svar(String behandlingId, String tema, String fritekst, Boolean sensitiv) {
        this.behandlingId = behandlingId;
        this.tema = tema;
        this.fritekst = fritekst;
        this.sensitiv = sensitiv != null? sensitiv : false;
    }

}
