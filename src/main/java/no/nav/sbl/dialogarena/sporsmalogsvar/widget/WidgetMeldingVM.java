package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagMeldingStatusTekstKey;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagStatusIkonKlasse;

public class WidgetMeldingVM extends MeldingVM implements FeedItemVM, Serializable {

    public WidgetMeldingVM(List<Melding> traad) {
        super(on(traad).collect(Melding.NYESTE_FORST).get(0), traad.size());
    }

    public static final Comparator<WidgetMeldingVM> NYESTE_OVERST = (o1, o2) -> o2.melding.opprettetDato.compareTo(o1.melding.opprettetDato);

    public String getMeldingStatusTekstKey() {
        return lagMeldingStatusTekstKey(melding);
    }

    public String getStatusIkonKlasse() {
        return lagStatusIkonKlasse(melding);
    }

    public String getOpprettetDato() {
        return WidgetDateFormatter.dateTime(melding.opprettetDato);
    }

    public String getFritekst() {
        return melding.fritekst;
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
