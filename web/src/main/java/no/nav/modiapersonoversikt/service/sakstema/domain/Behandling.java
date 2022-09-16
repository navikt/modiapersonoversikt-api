package no.nav.modiapersonoversikt.service.sakstema.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Behandling implements Serializable {
    public DateTime opprettetDato;
    public DateTime behandlingDato;
    public BehandlingsStatus behandlingsStatus;
    public BehandlingsType behandlingkvittering;
    public String behandlingsType;
    public Boolean ettersending;
    public String behandlingstema;
    public String behandlingsId;
    public String prefix;

    public Behandling withBehandlingKvittering(BehandlingsType behandlingkvittering) {
        this.behandlingkvittering = behandlingkvittering;
        return this;
    }

    public Behandling withBehandlingsId(String behandlingsId) {
        this.behandlingsId = behandlingsId;
        return this;
    }

    public Behandling withBehandlingsDato(DateTime behandlingDato) {
        this.behandlingDato = behandlingDato;
        return this;
    }

    public Behandling withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public Behandling withBehandlingStatus(BehandlingsStatus behandlingsStatus) {
        this.behandlingsStatus = behandlingsStatus;
        return this;
    }

    public Behandling withBehandlingsTema(String tema) {
        behandlingstema = tema;
        return this;
    }

    public Behandling withBehandlingsType(String behandlingsType) {
        this.behandlingsType = behandlingsType;
        return this;
    }

    public Behandling withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public DateTime getBehandlingDato() {
        return behandlingDato;
    }

    public BehandlingsStatus getBehandlingsStatus() {
        return behandlingsStatus;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }
}
