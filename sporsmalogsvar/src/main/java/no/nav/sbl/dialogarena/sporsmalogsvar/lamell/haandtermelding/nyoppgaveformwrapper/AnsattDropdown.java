package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

import static java.lang.String.format;

public class AnsattDropdown extends DropDownChoice<Ansatt> {
    public AnsattDropdown(String id, IModel<Ansatt> model, IModel<List<Ansatt>> choices) {
        super(id, model, choices, new AnsattChoiceRenderer());
        this.setOutputMarkupPlaceholderTag(true);
    }

    private static class AnsattChoiceRenderer implements IChoiceRenderer<Ansatt> {
        @Override
        public Object getDisplayValue(Ansatt ansatt) {
            return String.format("%s: %s %s", ansatt.ident, ansatt.fornavn, ansatt.etternavn);
        }

        @Override
        public String getIdValue(Ansatt ansatt, int i) {
            return ansatt.ident;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String placeholder = getString("ansattdropdown.null");
        String arialabel = getString("nyoppgave.form.label.tilansatt");
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').combobox({placeholder: '%s', arialabel: '%s'})", this.getMarkupId(), placeholder, arialabel + ", " + placeholder)));
        super.renderHead(response);
    }
}
