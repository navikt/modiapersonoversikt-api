package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Comparator;

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

    public String getOpprettetDato() {
        return Datoformat.kortMedTid(melding.opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.kortMedTid(melding.lestDato);
    }

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding);
    }

    public String getStatusIkonKlasse() {
        return lagStatusIkonKlasse(melding);
    }

    public String getJournalfortDatoFormatert() {
        return melding.journalfortDato == null ? "" : Datoformat.kort(melding.journalfortDato);
    }

    public boolean isJournalfort() {
        return melding.journalfortDato != null;
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
    public static AbstractReadOnlyModel<Boolean> traadLengdeStorreEnnEn(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return meldingVM.traadlengde > 1;
            }
        };
    }

}
