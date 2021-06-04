package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.emptyList;

public class Behandling implements Serializable {
    public DateTime opprettetDato;
    public DateTime behandlingDato;
    public BehandlingsStatus behandlingsStatus;
    public BehandlingsType behandlingkvittering;
    public String behandlingsType;
    public Boolean ettersending;
    public String behandlingstema;
    public String skjemanummerRef;
    public String behandlingsId;
    public String prefix;
    private List<DokumentFraHenvendelse> innsendteDokumenter = emptyList();
    private List<DokumentFraHenvendelse> manglendeDokumenter = emptyList();
    private String behandlingskjedeId;
    private HenvendelseType kvitteringstype;

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

    public Behandling withEttersending(Boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public Behandling withSkjemanummerRef(String skjemanummerRef) {
        this.skjemanummerRef = skjemanummerRef;
        return this;
    }

    public Behandling withKvitteringType(HenvendelseType kvitteringstype) {
        this.kvitteringstype = kvitteringstype;
        return this;
    }

    public Behandling withInnsendteDokumenter(List<DokumentFraHenvendelse> innsendteDokumenter) {
        this.innsendteDokumenter = innsendteDokumenter;
        return this;
    }

    public Behandling withManglendeDokumenter(List<DokumentFraHenvendelse> manglendeDokumenter) {
        this.manglendeDokumenter = manglendeDokumenter;
        return this;
    }

    public Behandling withBehandlingskjedeId(String id) {
        behandlingskjedeId = id;
        return this;
    }

    public DateTime getBehandlingDato() {
        return behandlingDato;
    }

    public BehandlingsStatus getBehandlingsStatus() {
        return behandlingsStatus;
    }

    public BehandlingsType getBehandlingkvittering() {
        return behandlingkvittering;
    }

    public String getBehandlingsType() {
        return behandlingsType;
    }

    public Boolean getEttersending() {
        return ettersending;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }

    public String getBehandlingsId() {
        return behandlingsId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSkjemanummerRef() {
        return skjemanummerRef;
    }

    public List<DokumentFraHenvendelse> getInnsendteDokumenter() {
        return innsendteDokumenter;
    }

    public List<DokumentFraHenvendelse> getManglendeDokumenter() {
        return manglendeDokumenter;
    }

    public String getBehandlingskjedeId() {
        return behandlingskjedeId;
    }

    public HenvendelseType getKvitteringstype() {
        return kvitteringstype;
    }
}
