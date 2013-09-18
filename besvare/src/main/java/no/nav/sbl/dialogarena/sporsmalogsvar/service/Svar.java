package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import java.io.Serializable;

public class Svar implements Serializable {
    private String behandlingId;
    public String tema, fritekst;
    public Boolean sensitiv;

    public Svar(String behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getBehandlingId() {
        return behandlingId;
    }
}
