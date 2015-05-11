package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.List;

import static java.lang.String.format;

public class EnhetDropdown extends Select<AnsattEnhet> {
    public EnhetDropdown(String id, IModel<AnsattEnhet> model, IModel<List<AnsattEnhet>> alle, IModel<List<AnsattEnhet>> foreslatte) {
        super(id, model);
        add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // må være her for at modellen blit oppdatert hver gang man endrer enhet
            }
        });

        add(new SelectOptions<>("foreslatte", foreslatte, new EnhetRenderer()).setRecreateChoices(true));
        add(new SelectOptions<>("alle", alle, new EnhetRenderer()).setRecreateChoices(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String placeholder = getString("ansattenhetdropdown.null");
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').combobox({placeholder: '%s'})", this.getMarkupId(), placeholder)));
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
