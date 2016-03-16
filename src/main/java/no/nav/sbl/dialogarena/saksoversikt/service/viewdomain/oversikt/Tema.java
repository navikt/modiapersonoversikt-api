package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;

public class Tema {
    private String temakode;
    private Behandling sistoppdatertebehandling;


    public Tema withSistoppdatertebehandling(Behandling sistoppdatertebehandling) {
        this.sistoppdatertebehandling = sistoppdatertebehandling;
        return this;
    }

    public Tema withTemakode(String temakode) {
        this.temakode = temakode;
        return this;
    }

    public Behandling getSistoppdatertebehandling() {
        return sistoppdatertebehandling;
    }

    public String getTemakode() {
        return temakode;
    }
}
