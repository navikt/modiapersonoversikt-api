package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

import org.joda.time.DateTime;

import java.util.List;

public class Soknad {
    public enum HenvendelseStatus {UNDER_ARBEID, FERDIG}

    private String behandlingsId;
    private String behandlingskjedeId;
    private String journalpostId;
    private HenvendelseStatus status;
    private DateTime opprettetDato;
    private DateTime innsendtDato;
    private DateTime sistendretDato;
    private String skjemanummerRef;
    private Boolean ettersending;
    private List<DokumentFraHenvendelse> dokumenter;
    private HenvendelseType type;

    public String getBehandlingsId() {
        return behandlingsId;
    }

    public String getBehandlingskjedeId() {
        return behandlingskjedeId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public HenvendelseStatus getStatus() {
        return status;
    }

    public DateTime getOpprettetDato() {
        return opprettetDato;
    }

    public DateTime getInnsendtDato() {
        return innsendtDato;
    }

    public DateTime getSistendretDato() {
        return sistendretDato;
    }

    public String getSkjemanummerRef() {
        return skjemanummerRef;
    }

    public Boolean getEttersending() {
        return ettersending;
    }

    public List<DokumentFraHenvendelse> getDokumenter() {
        return dokumenter;
    }

    public HenvendelseType getType() {
        return type;
    }

    public Soknad withBehandlingsId(String behandlingsId) {
        this.behandlingsId = behandlingsId;
        return this;
    }

    public Soknad withBehandlingskjedeId(String behandlingskjedeId) {
        this.behandlingskjedeId = behandlingskjedeId;
        return this;
    }

    public Soknad withJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
        return this;
    }

    public Soknad withStatus(HenvendelseStatus status) {
        this.status = status;
        return this;
    }

    public Soknad withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public Soknad withInnsendtDato(DateTime innsendtDato) {
        this.innsendtDato = innsendtDato;
        return this;
    }

    public Soknad withSistEndretDato(DateTime sistendretDato) {
        this.sistendretDato = sistendretDato;
        return this;
    }

    public Soknad withSkjemanummerRef(String skjemanummerRef) {
        this.skjemanummerRef = skjemanummerRef;
        return this;
    }

    public Soknad withEttersending(Boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public Soknad withHenvendelseType(HenvendelseType henvendelseType) {
        this.type = henvendelseType;
        return this;
    }

    public Soknad withDokumenter(List<DokumentFraHenvendelse> dokumenter) {
        this.dokumenter = dokumenter;
        return this;
    }
}


