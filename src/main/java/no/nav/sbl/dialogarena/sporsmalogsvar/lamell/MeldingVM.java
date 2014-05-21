package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

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
    public final int traadLengde;

    public MeldingVM(List<Melding> tilhorendeTraad, Melding melding) {
        this.melding = melding;

        avsender = lagMeldingOverskrift(tilhorendeTraad, melding);
        traadLengde = tilhorendeTraad.size();
    }

    public String getOpprettetDato() {
        return Datoformat.langMedTid(melding.opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.kortMedTid(melding.lestDato);
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
}
