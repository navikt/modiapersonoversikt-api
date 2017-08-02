package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.KategoriNotat.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning.INTERN;

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
    private Set<Baksystem> baksystem = new HashSet<>();
    private String temakode;
    private String temakodeVisning;
    private boolean ettersending;
    private boolean oversendtDokmot;
    private boolean erJournalfort = TRUE;
    private FeilWrapper feilWrapper = new FeilWrapper();
    private KategoriNotat kategoriNotat;


    public boolean isEttersending() {
        return ettersending;
    }

    public boolean isOversendtDokmot() {
        return oversendtDokmot;
    }

    public DokumentMetadata withEttersending(boolean ettersending) {
        this.ettersending = ettersending;
        return this;
    }

    public DokumentMetadata withOversendtDokmot(boolean oversendtDokmot) {
        this.oversendtDokmot = oversendtDokmot;
        return this;
    }

    public Set<Baksystem> getBaksystem() {
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

    public DokumentMetadata withTilhorendeSakid(final String tilhorendeSakid) {
        this.tilhorendeSakid = tilhorendeSakid;
        return this;
    }

    public DokumentMetadata withBaksystem(Baksystem baksystem) {
        this.baksystem.add(baksystem);
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

    public boolean isErJournalfort() {
        return erJournalfort;
    }

    public DokumentMetadata withKategoriNotat(String kategoriNotat) {
        if (this.getRetning().equals(INTERN)) {
            if (kategoriNotat.equals(FORVALTNINGSNOTAT.name()) || kategoriNotat.equals(REFERAT.name())) {
                this.kategoriNotat = FORVALTNINGSNOTAT;
            } else {
                this.kategoriNotat = INTERN_NOTAT;
            }
        }
        return this;
    }

    public KategoriNotat getKategoriNotat() {
        return kategoriNotat;
    }
}
