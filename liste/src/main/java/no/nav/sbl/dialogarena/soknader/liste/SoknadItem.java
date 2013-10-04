package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.soknader.liste.util.DateFormatter.printShortDate;
import static org.apache.wicket.model.Model.of;

public class SoknadItem extends Panel {

    public SoknadItem(String id, IModel<Soknad> model) {
        super(id, model);
        Soknad soknad = model.getObject();
        String innsendtDato = printShortDate(soknad.getInnsendtDato());
        String behandlingStart = printShortDate(soknad.getInnsendtDato());
        String ferdigdato = printShortDate(soknad.getFerdigDato());
        add(
                new Label("heading", soknad.getTittel()),
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIf(not(isEmptyString(of(innsendtDato))))),
                new Label("behandlingStart", "Under behandling siden  " + behandlingStart).add(visibleIf(not(isEmptyString(of(behandlingStart))))),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIf(not(isEmptyString(of(soknad.getNormertBehandlingsTid()))))),
                new Label("ferdigBehandlet", "Ferdig behandlet " + ferdigdato).add(visibleIf(not(isEmptyString(of(ferdigdato))))),
                new Label("status", soknad.getSoknadStatus())
        );
    }
}
