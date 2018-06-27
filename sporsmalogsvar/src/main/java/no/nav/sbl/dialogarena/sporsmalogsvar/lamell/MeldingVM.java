package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagMeldingStatusTekstKey;

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
        return melding.journalfortDato == null ? "" : WidgetDateFormatter.date(melding.journalfortDato);
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

    public IModel<Boolean> erBesvart() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return melding.erBesvart();
            }
        };
    }

    public Meldingstype getMeldingstype() {
        return melding.meldingstype;
    }

    public IModel<Boolean> erDokumentMelding() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return melding.erDokumentMelding;
            }
        };
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

    public String getAvsenderBildeUrl() {
        String imgUrl = WebApplication.get().getServletContext().getContextPath() + "/img/";
        if (erFraSaksbehandler()) {
            return imgUrl + NAV_LOGO_SVG;
        }
        return imgUrl + BRUKER_LOGO_SVG;
    }

    public String getAvsenderBildeAltKey() {
        if (erFraSaksbehandler()) {
            return NAV_AVSENDER_BILDE_ALT_KEY;
        }
        return BRUKER_AVSENDER_BILDE_ALT_KEY;
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
