package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Traad;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * Resolve overskrift for meldingsvisning i en tråd.
 */
public class MeldingOverskrift extends AbstractReadOnlyModel<String> {
    private IModel<Melding> melding;
    private IModel<Traad> traadmodell;

    public MeldingOverskrift(IModel<Melding> melding, IModel<Traad> traad) {
        this.melding = melding;
        this.traadmodell = traad;
    }

    @Override
    public String getObject() {
        return (forsteMeldingIDialog() ? "Melding fra " : "Svar fra ") + melding.getObject().avsender;
    }

    private boolean forsteMeldingIDialog() {
        Traad traad = this.traadmodell.getObject();
        return traad.getDialog().indexOf(melding.getObject()) == traad.getAntallMeldinger() - 1;
    }
}