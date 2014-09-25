package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import org.joda.time.DateTime;

import java.io.Serializable;

public class GenerellBehandling implements Serializable {

    public enum HenvendelseType {DOKUMENTINNSENDING, SOKNADSINNSENDING}
    public enum BehandlingsStatus {
        OPPRETTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }},
        AVSLUTTET { @Override public String cmsKey() { return "hendelse.sistoppdatert.dato"; }};
        public abstract String cmsKey();
    }

    public DateTime opprettetDato;
    public String behandlingsType;
    public DateTime behandlingDato;
    public BehandlingsStatus behandlingsStatus;
    public HenvendelseType henvendelseType;
    public String behandlingstema;
    public String sakstema;

    public GenerellBehandling withBehandlingsType(String type)  {
        behandlingsType = type;
        return this;
    }

    public GenerellBehandling withBehandlingsDato(DateTime behandlingDato) {
        this.behandlingDato = behandlingDato;
        return this;
    }

    public GenerellBehandling withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public GenerellBehandling withBehandlingStatus(BehandlingsStatus behandlingsStatus) {
        this.behandlingsStatus = behandlingsStatus;
        return this;
    }

    public GenerellBehandling withBehandlingsTema(String tema) {
        behandlingstema = tema;
        return this;
    }

    public GenerellBehandling withSaksTema(String tema) {
        sakstema = tema;
        return this;
    }

    public GenerellBehandling withHenvendelseType(HenvendelseType henvendelseType) {
        this.henvendelseType = henvendelseType;
        return this;
    }
}
