package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.lagMeldingOverskrift;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.NYESTE_FORST;

public class MeldingVM implements FeedItemVM, Serializable {

    public final String id;
    public final String avsender, tema;
    public final DateTime opprettetDato, lestDato;
    public final Status status;

    public MeldingVM(List<Melding> traad) {
        Melding melding = on(traad).collect(NYESTE_FORST).get(0);
        this.id = melding.id;
        this.avsender = lagMeldingOverskrift(traad, melding);
        this.tema = melding.tema;
        this.opprettetDato = melding.opprettetDato;
        this.lestDato = melding.lestDato;
        this.status = melding.status;
    }

    public static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };

    @Override
    public String getType() {
        return "meldinger";
    }

    @Override
    public String getId() {
        return id;
    }
}
