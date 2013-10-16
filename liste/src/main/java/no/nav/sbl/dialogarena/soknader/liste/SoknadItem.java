package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.Component;
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
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SOKNAD_STATUS_TRANSFORMER;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.MOTTATT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;
import static org.apache.wicket.model.Model.of;

public class SoknadItem extends Panel {

    private Soknad soknad;

    public SoknadItem(String id, IModel<Soknad> model) {
        super(id, model);
        soknad = model.getObject();
        String innsendtDato = optional(soknad.getInnsendtDato()).map(Datoformat.KORT).getOrElse("");
        add(
                new Label("heading", soknad.getTittel()),
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIfStringIsNotEmpty(innsendtDato)),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIfStringIsNotEmpty(soknad.getNormertBehandlingsTid()))
        );
        addLabelsBasedOnStatus();

    }

    private Component addLabelsBasedOnStatus() {
        Label statusLabel = new Label("status", "Ukjent status");
        Label icon = new Label("status-icon", "");
        DateTime date = null;

        if (soknad.getSoknadStatus() != null) {
            statusLabel.setDefaultModel(new StringResourceModel("soknad.status." + soknad.getSoknadStatus().name().toLowerCase(), new Model()));
        }

        if (soknadStatusIs(GAMMEL_FERDIG)) {
            icon.add(new AttributeAppender("class", new Model<>("gammel-ferdig"), " "));
            date = soknad.getFerdigDato();
        } else if (soknadStatusIs(MOTTATT)) {
            icon.add(new AttributeAppender("class", new Model<>("mottat"), " "));
            //date will still be null
        } else if (soknadStatusIs(NYLIG_FERDIG)) {
            icon.add(new AttributeAppender("class", new Model<>("nylig-ferdig"), " "));
            date = soknad.getFerdigDato();
        } else if (soknadStatusIs(UNDER_BEHANDLING)) {
            icon.add(new AttributeAppender("class", new Model<>("under-behandling"), " "));
            date = soknad.getUnderBehandlingStartDato();
        }
        return add(
                statusLabel,
                icon,
                new Label("status-date", optional(date).map(Datoformat.KORT).getOrElse(""))
        );
    }

    private Behavior visibleIfStringIsNotEmpty(String innsendtDato) {
        return visibleIf(not(isEmptyString(of(innsendtDato))));
    }

    private boolean soknadStatusIs(SoknadStatus status) {
        return status.equals(optional(soknad).map(SOKNAD_STATUS_TRANSFORMER).getOrElse(null));
    }


}
