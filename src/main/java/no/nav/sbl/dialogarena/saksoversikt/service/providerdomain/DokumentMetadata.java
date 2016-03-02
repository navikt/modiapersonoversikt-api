package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Entitet;

import java.time.LocalDateTime;
import java.util.List;

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
    private Baksystem baksystem;
    private String temakode;
    private String temakodeVisning;
    private boolean ettersending;
    private FeilWrapper feilWrapper = new FeilWrapper();

    public boolean isEttersending() {
        return ettersending;
    }

    public DokumentMetadata withEttersending(boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public Baksystem getBaksystem() {
        return baksystem;
    }

    public String getTemakode() {
        return temakode;
    }

    public Kommunikasjonsretning getRetning() {
        return retning;
    }

    public LocalDateTime getDato() {
        return dato;
    }

    public String getNavn() {
        return navn;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public Dokument getHoveddokument() {
        return hoveddokument;
    }

    public List<Dokument> getVedlegg() {
        return vedlegg;
    }

    public Entitet getAvsender() {
        return avsender;
    }

    public Entitet getMottaker() {
        return mottaker;
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

    public String getTilhorendeSakid() {
        return tilhorendeSakid;
    }

    public DokumentMetadata withTilhorendeSakid(final String tilhorendeSakid) {
        this.tilhorendeSakid = tilhorendeSakid;
        return this;
    }

    public DokumentMetadata withBaksystem(final Baksystem baksystem) {
        this.baksystem = baksystem;
        return this;
    }

    public String getTemakodeVisning() {
        return this.temakodeVisning;
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
}
