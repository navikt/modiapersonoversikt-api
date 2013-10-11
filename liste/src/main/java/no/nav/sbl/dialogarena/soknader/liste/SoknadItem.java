package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.wicket.model.Model.of;

public class SoknadItem extends Panel {

    public SoknadItem(String id, IModel<Soknad> model) {
        super(id, model);
        Soknad soknad = model.getObject();
        String innsendtDato = Optional.optional(soknad.getInnsendtDato()).map(Datoformat.KORT).getOrElse("");
        String behandlingStart = Optional.optional(soknad.getInnsendtDato()).map(Datoformat.KORT).getOrElse("");
        String ferdigdato = Optional.optional(soknad.getFerdigDato()).map(Datoformat.KORT).getOrElse("");
        add(
                new Label("heading", soknad.getTittel()),
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIfStringIsNotEmpty(innsendtDato)),
                new Label("behandlingStart", "Under behandling siden  " + behandlingStart).add(visibleIfStringIsNotEmpty(behandlingStart)),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIfStringIsNotEmpty(soknad.getNormertBehandlingsTid())),
                new Label("ferdigBehandlet", "Ferdig behandlet " + ferdigdato).add(visibleIfStringIsNotEmpty(ferdigdato)),
                new Label("status", soknad.getSoknadStatus())
        );
    }

    private Behavior visibleIfStringIsNotEmpty(String innsendtDato) {
        return visibleIf(not(isEmptyString(of(innsendtDato))));
    }

}
