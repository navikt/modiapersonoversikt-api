package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import java.util.List;

import static java.lang.String.format;

public class EnhetDropdown extends Select<AnsattEnhet> {

    public EnhetDropdown(String id, IModel<AnsattEnhet> model, final List<AnsattEnhet> alleEnheter, final List<AnsattEnhet> foreslatteEnheter) {
        super(id, model);

        IModel<List<AnsattEnhet>> alle = new ListModel<>(alleEnheter);
        IModel<List<AnsattEnhet>> foreslatte = new ListModel<>(foreslatteEnheter);

        add(new SelectOptions<>("foreslatte", foreslatte, new EnhetRenderer()).setRecreateChoices(true));
        add(new SelectOptions<>("alle", alle, new EnhetRenderer()).setRecreateChoices(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String placeholder = getString("ansattenhetdropdown.null");
        String arialabel = getString("nyoppgave.form.label.tilenhet");
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').combobox({placeholder: '%s', arialabel: '%s'})", this.getMarkupId(), placeholder, arialabel + ", " + placeholder)));
        super.renderHead(response);
    }

    static class EnhetRenderer implements IOptionRenderer<AnsattEnhet> {

        @Override
        public String getDisplayValue(AnsattEnhet enhet) {
            return String.format("%s %s", enhet.enhetId, enhet.enhetNavn);
        }

        @Override
        public IModel<AnsattEnhet> getModel(final AnsattEnhet value) {
            return new AbstractReadOnlyModel<AnsattEnhet>() {
                @Override
                public AnsattEnhet getObject() {
                    return value;
                }
            };
        }
    }
}
