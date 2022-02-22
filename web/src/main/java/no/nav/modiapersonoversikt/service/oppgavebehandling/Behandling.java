package no.nav.modiapersonoversikt.service.oppgavebehandling;

import static java.lang.String.format;

public class Behandling {

    private String behandlingstema;
    private String behandlingstype;

    public Behandling() {}

    public Behandling(String behandlingstema, String behandlingstype) {
        this.behandlingstema = behandlingstema;
        this.behandlingstype = behandlingstype;
    }

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

    @Override
    public String toString() {
        return format("[behandlingstema=%s, behandlingstype=%s]", behandlingstema, behandlingstype);
    }
}
