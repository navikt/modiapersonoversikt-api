package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class JournalpostInformasjon extends AnimertPanel {
    public JournalpostInformasjon(String id, IModel<MeldingVM> model) {
        super(id);
        setDefaultModel(model);
        setOutputMarkupPlaceholderTag(true);
        add(
                new Label("journalfortDatoFormatert"),
                new Label("melding.journalfortTemanavn"),
                new Label("melding.journalfortAvNavIdent"),
                new Label("melding.journalfortSaksId"));
    }
}
