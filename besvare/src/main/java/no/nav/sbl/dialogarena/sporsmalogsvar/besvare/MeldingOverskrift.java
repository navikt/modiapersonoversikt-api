package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * Resolver overskrift for meldingsvisning i en tråd.
 */
public class MeldingOverskrift extends AbstractReadOnlyModel<String> {
    private IModel<Melding> melding;
    private IModel<Traad> traad;

    public MeldingOverskrift(IModel<Melding> melding, IModel<Traad> traad) {
        this.melding = melding;
        this.traad = traad;
    }

    @Override
    public String getObject() {
        return (melding.getObject().innleder(traad.getObject()) ? "Melding fra " : "Svar fra ") + melding.getObject().avsender;
    }

}