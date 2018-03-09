package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class JournalpostInformasjon extends AnimertPanel {
    public JournalpostInformasjon(String id, IModel<MeldingVM> model) {
        super(id);
        setDefaultModel(model);
        setOutputMarkupPlaceholderTag(true);

        String format = "%s %s (%s) | %s | %s";
        if (meldingHarSaksId(model)) {
            format = "%s %s (%s) | %s | %s | %s %s";
        }

        add(new Label("tekst", new StringFormatModel(
                format,
                new StringResourceModel("journalpost.journalfort.av", this, null),
                new PropertyModel<>(model, "melding.journalfortAv.navn"),
                new PropertyModel<>(model, "melding.journalfortAvNavIdent"),
                new PropertyModel<>(model, "journalfortDatoFormatert"),
                new PropertyModel<>(model, "melding.journalfortTemanavn"),
                new StringResourceModel("journalpost.jornalfort.saksid", this, null),
                new PropertyModel<>(model, "melding.journalfortSaksId")
        )));
    }

    private static boolean meldingHarSaksId(final IModel<MeldingVM> model) {
        return model.getObject().melding.journalfortSaksId != null;
    }
}
