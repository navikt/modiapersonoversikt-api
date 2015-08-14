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
                new PropertyModel<String>(model, "melding.journalfortAv.navn"),
                new PropertyModel<String>(model, "melding.journalfortAvNavIdent"),
                new PropertyModel<String>(model, "journalfortDatoFormatert"),
                new PropertyModel<String>(model, "melding.journalfortTemanavn"),
                new StringResourceModel("journalpost.jornalfort.saksid", this, null),
                new PropertyModel<String>(model, "melding.journalfortSaksId")
        )));
    }

    private static boolean meldingHarSaksId(final IModel<MeldingVM> model) {
        return model.getObject().melding.journalfortSaksId != null;
    }
}
