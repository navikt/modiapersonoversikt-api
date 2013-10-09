package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
                new Label("innsendtDato", "Innsendt " + innsendtDato).add(visibleIfStringIsNotEmpty(innsendtDato)),
                new Label("behandlingStart", "Under behandling siden  " + behandlingStart).add(visibleIfStringIsNotEmpty(behandlingStart)),
                new Label("behandlingsTid", "Normert behandlingstid " + soknad.getNormertBehandlingsTid()).add(visibleIfStringIsNotEmpty(soknad.getNormertBehandlingsTid())),
                new Label("ferdigBehandlet", "Ferdig behandlet " + ferdigdato).add(visibleIfStringIsNotEmpty(ferdigdato)),
                createStatusLabel(soknad),
                createStatusIcon(soknad)
        );
    }

    private Behavior visibleIfStringIsNotEmpty(String innsendtDato) {
        return visibleIf(not(isEmptyString(of(innsendtDato))));
    }

    private Component createStatusLabel(Soknad soknad){
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.GAMMEL_FERDIG)){
            return new Label("status", "Gammel");
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.MOTTATT)){
            return new Label("status", "Mottat");
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.NYLIG_FERDIG)){
            return new Label("status", "Nylig ferdig");
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.UNDER_BEHANDLING)){
            return new Label("status", "Under behandling");
        }
        return new Label("status", "Ukjent status");


    }

    private Component createStatusIcon(Soknad soknad){
        Label icon = new Label("status-icon", "");
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.GAMMEL_FERDIG)){
            icon.add(new AttributeAppender("class",new Model<>("gammel-ferdig"), " "));
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.MOTTATT)){
            icon.add(new AttributeAppender("class",new Model<>("mottat"), " "));
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.NYLIG_FERDIG)){
            icon.add(new AttributeAppender("class",new Model<>("nylig-ferdig"), " "));
        }
        if(soknad.getSoknadStatus().equals(Soknad.SoknadStatus.UNDER_BEHANDLING)){
            icon.add(new AttributeAppender("class",new Model<>("under-behandling"), " "));
        }
        return icon;
    }

}
