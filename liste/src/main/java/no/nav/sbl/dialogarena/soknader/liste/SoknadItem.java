package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static org.apache.wicket.model.Model.of;

public class SoknadItem extends Panel {

    private Soknad soknad;

    public SoknadItem(String id, IModel<Soknad> model) {
        super(id, model);
        soknad = model.getObject();
        String innsendtDato = optional(soknad.getInnsendtDato()).map(KORT).get();
        add(
                new Label("heading", soknad.getTittelKodeverk()),
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIfStringIsNotEmpty(innsendtDato)),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIfStringIsNotEmpty(soknad.getNormertBehandlingsTid()))
        );
        addLabelsBasedOnStatus();

    }

    private void addLabelsBasedOnStatus() {
        Label icon = new Label("status-icon", "");
        DateTime date = evaluateAttributes(icon);

        add(
                new Label("status", new StringResourceModel("soknad.status." + soknad.getSoknadStatus().name().toLowerCase(), new Model())),
                icon,
                new Label("status-date", optional(date).map(KORT).get())
        );
    }

    private DateTime evaluateAttributes(Label icon) {
        switch (soknad.getSoknadStatus()) {
            case GAMMEL_FERDIG:
                return setGammelFerdigAttributes(icon);
            case NYLIG_FERDIG:
                return setNyligFerdigAttributes(icon);
            case UNDER_BEHANDLING:
                return setUnderBehandlingAttributes(icon);
            case MOTTATT:
                return setMottattAttributes(icon);
            default:
                throw new ApplicationException("soknadsstatus cannot be unknown");
        }
    }

    private DateTime setNyligFerdigAttributes(Label icon) {
        icon.add(new AttributeAppender("class", new Model<>("nylig-ferdig"), " "));
        return soknad.getFerdigDato();
    }

    private DateTime setUnderBehandlingAttributes(Label icon) {
        icon.add(new AttributeAppender("class", new Model<>("under-behandling"), " "));
        return soknad.getUnderBehandlingStartDato();
    }

    private DateTime setMottattAttributes(Label icon) {
        icon.add(new AttributeAppender("class", new Model<>("mottatt"), " "));
        return soknad.getInnsendtDato();
    }

    private DateTime setGammelFerdigAttributes(Label icon) {
        icon.add(new AttributeAppender("class", new Model<>("gammel-ferdig"), " "));
        return soknad.getFerdigDato();
    }

    private Behavior visibleIfStringIsNotEmpty(String innsendtDato) {
        return visibleIf(not(isEmptyString(of(innsendtDato))));
    }

}
