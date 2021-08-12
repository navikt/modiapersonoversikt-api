package no.nav.modiapersonoversikt.legacy.sak.providerdomain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.TRUE;

public class DokumentMetadata {

    private Kommunikasjonsretning retning;
    private LocalDateTime dato;
    private String navn;
    private String journalpostId;
    private Dokument hoveddokument;
    private List<Dokument> vedlegg;
    private Entitet avsender;
    private Entitet mottaker;
    private String tilhorendeSakid;
    private String tilhorendeFagsakId;
    private String behandlingsId;
    private Set<Baksystem> baksystem = new HashSet<>();
    private String temakode;
    private String temakodeVisning;
    private boolean ettersending;
    private boolean erJournalfort = TRUE;
    private String kanalNavn;
    private KanalType kanalType;
    public String antallRetur;

    private FeilWrapper feilWrapper = new FeilWrapper();

    public KanalType getKanalType() {
        return kanalType;
    }

    public void setKanalType(KanalType kanalType) {
        this.kanalType = kanalType;
    }

    public boolean isEttersending() {
        return ettersending;
    }

    public void setEttersending(boolean ettersending) {
        this.ettersending = ettersending;
    }

    public DokumentMetadata withEttersending(boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public Set<Baksystem> getBaksystem() {
        return baksystem;
    }

    public void setBaksystem(Set<Baksystem> baksystem) {
        this.baksystem = baksystem;
    }

    public String getTemakode() {
        return temakode;
    }

    public void setTemakode(String temakode) {
        this.temakode = temakode;
    }

    public Kommunikasjonsretning getRetning() {
        return retning;
    }

    public void setRetning(Kommunikasjonsretning retning) {
        this.retning = retning;
    }

    public LocalDateTime getDato() {
        return dato;
    }

    public void setDato(LocalDateTime dato) {
        this.dato = dato;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public Dokument getHoveddokument() {
        return hoveddokument;
    }

    public void setHoveddokument(Dokument hoveddokument) {
        this.hoveddokument = hoveddokument;
    }

    public List<Dokument> getVedlegg() {
        return vedlegg;
    }

    public void setVedlegg(List<Dokument> vedlegg) {
        this.vedlegg = vedlegg;
    }

    public Entitet getAvsender() {
        return avsender;
    }

    public void setAvsender(Entitet avsender) {
        this.avsender = avsender;
    }

    public Entitet getMottaker() {
        return mottaker;
    }

    public void setMottaker(Entitet mottaker) {
        this.mottaker = mottaker;
    }

    public String getKanalNavn() {
        return kanalNavn;
    }

    public void setKanalNavn(String kanalNavn) {
        this.kanalNavn = kanalNavn;
    }

    public String getAntallRetur() {
        return antallRetur;
    }

    public void setAntallRetur(String antallRetur) {
        this.antallRetur = antallRetur;
    }

    public DokumentMetadata withRetning(final Kommunikasjonsretning retning) {
        this.retning = retning;
        return this;
    }

    public DokumentMetadata withDato(final LocalDateTime dato) {
        this.dato = dato;
        return this;
    }

    public DokumentMetadata withNavn(final String navn) {
        this.navn = navn;
        return this;
    }

    public DokumentMetadata withJournalpostId(final String journalpostId) {
        this.journalpostId = journalpostId;
        return this;
    }

    public DokumentMetadata withHoveddokument(final Dokument hoveddokument) {
        this.hoveddokument = hoveddokument;
        return this;
    }

    public DokumentMetadata withVedlegg(final List<Dokument> vedlegg) {
        this.vedlegg = vedlegg;
        return this;
    }

    public DokumentMetadata withAvsender(final Entitet avsender) {
        this.avsender = avsender;
        return this;
    }

    public DokumentMetadata withMottaker(final Entitet mottaker) {
        this.mottaker = mottaker;
        return this;
    }

    public DokumentMetadata withTemakode(final String temakode) {
        this.temakode = temakode;
        return this;
    }

    public DokumentMetadata withIsJournalfort(final boolean erJournalfort) {
        this.erJournalfort = erJournalfort;
        return this;
    }

    public DokumentMetadata withLeggTilEttersendelseTekstDersomEttersendelse() {
        if (this.ettersending) {
            this.hoveddokument.withTittel("Ettersendelse til " + this.hoveddokument.getTittel());
        }
        return this;
    }

    public String getTilhorendeSakid() {
        return tilhorendeSakid;
    }

    public void setTilhorendeSakid(String tilhorendeSakid) {
        this.tilhorendeSakid = tilhorendeSakid;
    }

    public DokumentMetadata withTilhorendeSakid(final String tilhorendeSakid) {
        this.tilhorendeSakid = tilhorendeSakid;
        return this;
    }

    public String getBehandlingsId() {
        return behandlingsId;
    }

    public void setBehandlingsId(String behandlingsId) {
        this.behandlingsId = behandlingsId;
    }

    public DokumentMetadata withBehandlingsId(String behandlingsId) {
        this.behandlingsId = behandlingsId;
        return this;
    }

    public DokumentMetadata withBaksystem(Baksystem baksystem) {
        this.baksystem.add(baksystem);
        return this;
    }

    public String getTemakodeVisning() {
        return this.temakodeVisning;
    }

    public void setTemakodeVisning(String temakodeVisning) {
        this.temakodeVisning = temakodeVisning;
    }

    public DokumentMetadata withTemakodeVisning(final String temakodeVisning) {
        this.temakodeVisning = temakodeVisning;
        return this;
    }

    public DokumentMetadata withFeilWrapper(final Feilmelding feilmelding) {
        this.feilWrapper = new FeilWrapper(feilmelding);
        return this;
    }

    public FeilWrapper getFeilWrapper() {
        return feilWrapper;
    }

    public void setFeilWrapper(FeilWrapper feilWrapper) {
        this.feilWrapper = feilWrapper;
    }

    public boolean isErJournalfort() {
        return erJournalfort;
    }

    public void setErJournalfort(boolean erJournalfort) {
        this.erJournalfort = erJournalfort;
    }

    public String getTilhorendeFagsakId() {
        return tilhorendeFagsakId;
    }

    public void setTilhorendeFagsakId(String tilhorendeFagsakId) {
        this.tilhorendeFagsakId = tilhorendeFagsakId;
    }

    public DokumentMetadata withTilhorendeFagsakId(String tilhorendeFagsakId) {
        this.tilhorendeFagsakId = tilhorendeFagsakId;
        return this;
    }
}
