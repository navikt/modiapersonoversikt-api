package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Behandling implements Serializable {
    public enum BehandlingsType {BEHANDLING, KVITTERING}

    public enum BehandlingsStatus {
        OPPRETTET {
            public String cmsKey() {
                return "hendelse.sistoppdatert.dato";
            }
        },
        AVBRUTT {
            public String cmsKey() {
                return "hendelse.sistoppdatert.dato";
            }
        },
        AVSLUTTET {
            public String cmsKey() {
                return "hendelse.sistoppdatert.dato";
            }
        };
        public abstract String cmsKey();
    }

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


    //Fra kvittering
    private List<DokumentFraHenvendelse> innsendteDokumenter;
    private List<DokumentFraHenvendelse> manglendeDokumenter;
    private String behandlingskjedeId;
    private String journalpostId;
    private HenvendelseType kvitteringstype;
    private Optional<String> arkivreferanseOriginalkvittering;

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

    public DateTime getOpprettetDato() {
        return opprettetDato;
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

    public String getJournalpostId() {
        return journalpostId;
    }

    public HenvendelseType getKvitteringstype() {
        return kvitteringstype;
    }

    public Optional<String> getArkivreferanseOriginalkvittering() {
        return arkivreferanseOriginalkvittering;
    }

    public Behandling withKvitteringType(HenvendelseType kvitteringstype) {
        this.kvitteringstype = kvitteringstype;
        return this;
    }

    public Behandling withArkivreferanseOriginalkvittering(Optional<String> arkivreferanseOriginalkvittering) {
        this.arkivreferanseOriginalkvittering = arkivreferanseOriginalkvittering;
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

    public Behandling withJournalPostId(String id) {
        journalpostId = id;
        return this;
    }

}
