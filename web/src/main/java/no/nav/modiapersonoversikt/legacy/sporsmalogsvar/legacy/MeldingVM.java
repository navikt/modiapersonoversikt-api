package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy;

import no.nav.modiapersonoversikt.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.api.domain.henvendelse.Meldingstype;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.common.utils.DateUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.modiapersonoversikt.api.utils.VisningUtils.lagMeldingStatusTekstKey;

public class MeldingVM implements Serializable {

    public static final String NAV_LOGO_SVG = "nav-logo.svg";
    public static final String BRUKER_LOGO_SVG = "meldinger/personikon.svg";
    public static final String NAV_AVSENDER_BILDE_ALT_KEY = "innboks.avsender.nav";
    public static final String BRUKER_AVSENDER_BILDE_ALT_KEY = "innboks.avsender.bruker";
    public final Melding melding;

    public final int traadlengde;
    public boolean erDokumentMelding;
    public boolean erOppgaveMelding;

    public MeldingVM(Melding melding, int traadLengde) {
        this.melding = melding;
        this.traadlengde = traadLengde;
        this.erDokumentMelding = melding.erDokumentMelding;
        this.erOppgaveMelding = melding.erOppgaveMelding;
    }

    public String getVisningsDato() {
        return DateUtils.toString(melding.ferdigstiltDato);
    }

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding.meldingstype);
    }

    public String getJournalfortDatoFormatert() {
        return melding.journalfortDato == null ? "" : DateUtils.date(melding.journalfortDato);
    }

    public boolean isJournalfort() {
        return melding.journalfortDato != null;
    }

    public Boolean erFeilsendt() {
        return getMarkertSomFeilsendtAv().isPresent();
    }

    public boolean erKontorsperret() {
        return melding.kontorsperretEnhet != null;
    }

    public boolean erFerdigstiltUtenSvar() {
        return melding.erFerdigstiltUtenSvar;
    }

    public boolean erBesvart() {
        return melding.erBesvart();
    }

    public Meldingstype getMeldingstype() {
        return melding.meldingstype;
    }

    public Boolean erDokumentMelding() {
        return melding.erDokumentMelding;
    }

    public Optional<Saksbehandler> getMarkertSomFeilsendtAv() {
        return ofNullable(melding.markertSomFeilsendtAv);
    }

    public Optional<DateTime> getMarkertSomFeilsendtDato() {
        return ofNullable(melding.markertSomFeilsendtDato);
    }

    public Optional<Saksbehandler> getKontorsperretAv() {
        return ofNullable(melding.kontorsperretAv);
    }

    public Optional<DateTime> getKontorsperretDato() {
        return ofNullable(melding.kontorsperretDato);
    }

    public Optional<Saksbehandler> getFerdigstiltUtenSvarAv() {
        return ofNullable(melding.ferdigstiltUtenSvarAv);
    }

    public Optional<DateTime> getFerdigstiltUtenSvarDato() {
        return ofNullable(melding.ferdigstiltUtenSvarDato);
    }

    public String getId() {
        return melding.id;
    }

    public String getTraadId() {
        return melding.traadId;
    }

    public DateTime getDato() {
        return melding.getVisningsDato();
    }

    public boolean erFraSaksbehandler() {
        return melding.erFraSaksbehandler();
    }

    public boolean erSporsmal() {
        return melding.erSporsmal();
    }

    public boolean erDelsvar() {
        return melding.erDelvisSvar();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MeldingVM && this.melding.id.equals(((MeldingVM) obj).melding.id);
    }

    @Override
    public int hashCode() {
        int result = melding.hashCode();
        result = 31 * result + traadlengde;
        return result;
    }

}
