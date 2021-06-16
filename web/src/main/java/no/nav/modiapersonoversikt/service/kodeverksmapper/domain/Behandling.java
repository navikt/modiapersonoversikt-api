package no.nav.modiapersonoversikt.service.kodeverksmapper.domain;

import static java.lang.String.format;

public class Behandling {

    private String behandlingstema;
    private String behandlingstype;

    public String getBehandlingstema() {
        return this.behandlingstema;
    }

    public String getBehandlingstype() {
        return this.behandlingstype;
    }

    public void setBehandlingstema(String behandlingstema) {
        this.behandlingstema = behandlingstema;
    }

    public void setBehandlingstype(String behandlingstype) {
        this.behandlingstype = behandlingstype;
    }

    public Behandling withBehandlingstema(String behandlingstema) {
        setBehandlingstema(behandlingstema);
        return this;
    }

    public Behandling withBehandlingstype(String behandlingstype) {
        setBehandlingstype(behandlingstype);
        return this;
    }

    @Override
    public String toString() {
        return format("[behandlingstema=%s, behandlingstype=%s]", behandlingstema, behandlingstype);
    }
}
