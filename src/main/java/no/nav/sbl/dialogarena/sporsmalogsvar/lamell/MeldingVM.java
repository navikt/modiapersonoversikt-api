package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.protocol.http.WebApplication;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.FRA_NAV;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.lagMeldingStatusTekstKey;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.lagStatusIkonKlasse;


public class MeldingVM implements Serializable {

    public final Melding melding;

    public final int traadlengde;
    public boolean nyesteMeldingISinJournalfortgruppe;

    public MeldingVM(Melding melding, int traadLengde) {
        this.melding = melding;
        this.traadlengde = traadLengde;
    }

    public String getAvsenderTekst() {
        return WidgetDateFormatter.dateTime(melding.opprettetDato)
                + (melding.navIdent != null ? " - " + melding.navIdent : "");
    }

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding);
    }

    public String getStatusIkonKlasse() {
        return lagStatusIkonKlasse(melding);
    }

    public String getJournalfortDatoFormatert() {
        return melding.journalfortDato == null ? "" : WidgetDateFormatter.date(melding.journalfortDato);
    }

    public boolean isJournalfort() {
        return melding.journalfortDato != null;
    }

    public String getTemagruppeKey() {
        return melding.temagruppe != null ? melding.temagruppe : "temagruppe.kassert";
    }

    public Boolean erFeilsendt() {
        return getMarkertSomFeilsendtAv().isSome();
    }

    public Optional<String> getMarkertSomFeilsendtAv() {
        return optional(melding.markertSomFeilsendtAv);
    }

    public String getAvsenderBildeUrl() {
        String imgUrl = WebApplication.get().getServletContext().getContextPath() + "/img/";
        if (FRA_NAV.contains(melding.meldingstype)) {
            return imgUrl + "nav-logo.svg";
        }
        return imgUrl + "siluett.svg";
    }

    public String getAvsenderBildeAltKey() {
        if (FRA_NAV.contains(melding.meldingstype)) {
            return "innboks.avsender.nav";
        }
        return "innboks.avsender.bruker";
    }

    public static final Comparator<MeldingVM> NYESTE_FORST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.melding.opprettetDato.compareTo(o1.melding.opprettetDato);
        }
    };

    public static final Transformer<MeldingVM, String> ID = new Transformer<MeldingVM, String>() {
        @Override
        public String transform(MeldingVM meldingVM) {
            return meldingVM.melding.id;
        }
    };

    public static final Transformer<MeldingVM, String> TRAAD_ID = new Transformer<MeldingVM, String>() {
        @Override
        public String transform(MeldingVM meldingVM) {
            return meldingVM.melding.traadId;
        }
    };

    public static final Transformer<MeldingVM, Boolean> FEILSENDT = new Transformer<MeldingVM, Boolean>() {
        @Override
        public Boolean transform(MeldingVM meldingVM) {
            return meldingVM.erFeilsendt();
        }
    };

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

    public static final Transformer<MeldingVM, LocalDate> JOURNALFORT_DATO = new Transformer<MeldingVM, LocalDate>() {
        @Override
        public LocalDate transform(MeldingVM meldingVM) {
            if (meldingVM.melding.journalfortDato != null) {
                return meldingVM.melding.journalfortDato.toLocalDate();
            }
            return null;
        }
    };

}
