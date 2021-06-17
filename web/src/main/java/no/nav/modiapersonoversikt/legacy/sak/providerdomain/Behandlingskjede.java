package no.nav.modiapersonoversikt.legacy.sak.providerdomain;

import java.time.LocalDateTime;

public class Behandlingskjede {
    private BehandlingsStatus status;
    private LocalDateTime sistOppdatert;

    public Behandlingskjede withStatus(BehandlingsStatus status){
        this.status = status;
        return this;
    }

    public Behandlingskjede withSistOppdatert(LocalDateTime sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
        return this;
    }

    public BehandlingsStatus getStatus() {
        return status;
    }

    public LocalDateTime getSistOppdatert() {
        return sistOppdatert;
    }
}
