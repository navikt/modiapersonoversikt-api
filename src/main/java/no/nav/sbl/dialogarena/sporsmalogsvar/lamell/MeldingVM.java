package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.lagMeldingOverskrift;


public class MeldingVM implements Serializable {

    public final Melding melding;

    public final String avsender;
    public final int traadlengde;

    public MeldingVM(List<Melding> tilhorendeTraad, Melding melding) {
        this.melding = melding;

        avsender = lagMeldingOverskrift(tilhorendeTraad, melding);
        traadlengde = tilhorendeTraad.size();
    }

    public String getOpprettetDato() {
        return Datoformat.kortMedTid(melding.opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.kortMedTid(melding.lestDato);
    }

    public String getStatusKlasse() {
        return VisningUtils.getStatusKlasse(melding.status);
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MeldingVM && this.melding.id.equals(((MeldingVM) obj).melding.id);
    }

    @Override
    public int hashCode() {
        int result = melding.hashCode();
        result = 31 * result + avsender.hashCode();
        result = 31 * result + traadlengde;
        return result;
    }
}
