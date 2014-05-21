package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.lagMeldingOverskrift;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.NYESTE_FORST;

public class MeldingVM implements FeedItemVM, Serializable {

    public final Melding melding;

    public final String avsender;

    public MeldingVM(List<Melding> traad) {
        this.melding = on(traad).collect(NYESTE_FORST).get(0);
        this.avsender = lagMeldingOverskrift(traad, melding);
    }

    public static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.melding.opprettetDato.compareTo(o1.melding.opprettetDato);
        }
    };

    public String getOpprettetDato() {
        return Datoformat.langMedTid(melding.opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.ultrakort(melding.lestDato);
    }


    @Override
    public String getType() {
        return "meldinger";
    }

    @Override
    public String getId() {
        return melding.id;
    }
}
