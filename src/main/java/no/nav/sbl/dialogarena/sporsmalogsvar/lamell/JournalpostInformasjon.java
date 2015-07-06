package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class JournalpostInformasjon extends AnimertPanel {
    public JournalpostInformasjon(String id, IModel<MeldingVM> model) {
        super(id);
        setDefaultModel(model);
        setOutputMarkupPlaceholderTag(true);
        add(
                new Label("melding.journalfortAv.navn"),
                new Label("melding.journalfortAvNavIdent"),
                new Label("journalfortDatoFormatert"),
                new Label("melding.journalfortTemanavn"),
                new WebMarkupContainer("saksIdContainer")
                        .add(new Label("melding.journalfortSaksId"))
                        .add(visibleIf(meldingHarSaksId(model)))
        );
    }

    private static AbstractReadOnlyModel<Boolean> meldingHarSaksId(final IModel<MeldingVM> model) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return model.getObject().melding.journalfortSaksId != null;
            }
        };
    }
}
