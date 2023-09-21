package no.nav.modiapersonoversikt.service.sakstema.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Behandlingskjede {
    private BehandlingsStatus status;
    private LocalDateTime sistOppdatert;

    private String behandlingId;

    public Behandlingskjede withStatus(BehandlingsStatus status){
        this.status = status;
        return this;
    }

    public Behandlingskjede withSistOppdatert(LocalDateTime sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
        return this;
    }

    public Behandlingskjede withBehandlingId(String behandlingId) {
        this.behandlingId = behandlingId;
        return this;
    }

    public BehandlingsStatus getStatus() {
        return status;
    }

    public LocalDateTime getSistOppdatert() {
        return sistOppdatert;
    }

    public String getBehandlingId() {
        return behandlingId;
    }
}
