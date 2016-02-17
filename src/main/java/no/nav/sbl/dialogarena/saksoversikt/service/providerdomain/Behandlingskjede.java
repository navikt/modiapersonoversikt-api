package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import org.joda.time.DateTime;

public class Behandlingskjede {
    private BehandlingsStatus status;
    private DateTime sistOppdatert;

    public Behandlingskjede withStatus(BehandlingsStatus status){
        this.status = status;
        return this;
    }

    public Behandlingskjede withSistOppdatert(DateTime sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
        return this;
    }
}
