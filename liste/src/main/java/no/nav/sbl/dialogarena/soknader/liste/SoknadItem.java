package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.soknader.widget.util.SoknadDateFormatter.printShortDate;

public class SoknadItem extends Panel {

    public SoknadItem(String id, IModel<Soknad> model) {
        super(id, model);
        Soknad soknad = model.getObject();
        String innsendtDato = printShortDate(soknad.getMottattDato());
        String behandlingStart = printShortDate(soknad.getMottattDato());
        String ferdigdato = printShortDate(soknad.getFerdigDato());
        add(new Label("heading", soknad.getTittel()),
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIf(not(isEmptyString(Model.of(innsendtDato))))),
                new Label("behandlingStart", "Under behandling siden  " + behandlingStart).add(visibleIf(not(isEmptyString(Model.of(behandlingStart))))),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIf(not(isEmptyString(Model.of(soknad.getNormertBehandlingsTid()))))),
                new Label("ferdigBehandlet", "Ferdig behandlet " + ferdigdato).add(visibleIf(not(isEmptyString(Model.of(ferdigdato)))))

        );


    }

}


//visibleIf(not(isEmptyString(new PropertyModel<String>(getModel(), "metaheading"))))),
