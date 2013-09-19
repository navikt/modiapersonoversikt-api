package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import org.apache.wicket.model.CompoundPropertyModel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.Aarsak.ANNEN;

public class LeggTilbakeModell extends CompoundPropertyModel<AarsakVM> {

    public LeggTilbakeModell(AarsakVM object) {
        super(object);
    }

    public String getAarsakForTilbakeleggelse() {
        if (getObject().getValg() == ANNEN) {
            return getObject().getAnnenAarsakTekst();
        }
        return getObject().getValg().toString();
    }
}
