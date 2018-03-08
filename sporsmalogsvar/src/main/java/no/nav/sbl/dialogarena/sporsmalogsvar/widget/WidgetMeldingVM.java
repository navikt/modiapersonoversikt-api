package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagMeldingStatusTekstKey;

public class WidgetMeldingVM extends MeldingVM implements FeedItemVM, Serializable {

    public final boolean erMonolog;

    public WidgetMeldingVM(List<Melding> traad, boolean erMonolog) {
        super(traad.stream().sorted(Melding.NYESTE_FORST).findFirst().get(), traad.size());
        this.erMonolog = erMonolog;
    }

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding.meldingstype);
    }

    public String getVisningsDato() {
        return DateUtils.toString(melding.getVisningsDato());
    }

    public DateTime getDato() {
        return melding.getVisningsDato();
    }

    public String getFritekst() {
        return melding.getFritekst();
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
