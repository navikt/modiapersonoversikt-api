package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_NAV;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagMeldingStatusTekstKey;

public class MeldingVM implements Serializable {

    public static final String NAV_LOGO_SVG = "nav-logo.svg";
    public static final String BRUKER_LOGO_SVG = "personikon.svg";
    public static final String NAV_AVSENDER_BILDE_ALT_KEY = "innboks.avsender.nav";
    public static final String BRUKER_AVSENDER_BILDE_ALT_KEY = "innboks.avsender.bruker";
    public final Melding melding;

    public final int traadlengde;

    public MeldingVM(Melding melding, int traadLengde) {
        this.melding = melding;
        this.traadlengde = traadLengde;
    }

    public String getAvsenderDato() {
        return DateUtils.dateTime(melding.opprettetDato);
    }

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding);
    }

    public String getJournalfortDatoFormatert() {
        return melding.journalfortDato == null ? "" : WidgetDateFormatter.date(melding.journalfortDato);
    }

    public boolean isJournalfort() {
        return melding.journalfortDato != null;
    }

    public Boolean erFeilsendt() {
        return getMarkertSomFeilsendtAv().isSome();
    }

    public boolean erKontorsperret() {
        return melding.kontorsperretEnhet != null;
    }

    public IModel<Boolean> erBesvart() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return melding.status != Status.IKKE_BESVART;
            }
        };
    }

    public Optional<String> getMarkertSomFeilsendtAv() {
        return optional(melding.markertSomFeilsendtAv);
    }

    public String getAvsenderBildeUrl() {
        String imgUrl = WebApplication.get().getServletContext().getContextPath() + "/img/";
        if (FRA_NAV.contains(melding.meldingstype)) {
            return imgUrl + NAV_LOGO_SVG;
        }
        return imgUrl + BRUKER_LOGO_SVG;
    }

    public String getAvsenderBildeAltKey() {
        if (FRA_NAV.contains(melding.meldingstype)) {
            return NAV_AVSENDER_BILDE_ALT_KEY;
        }
        return BRUKER_AVSENDER_BILDE_ALT_KEY;
    }

    public boolean erFraSaksbehandler() {
        return FRA_NAV.contains(melding.meldingstype);
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
}
