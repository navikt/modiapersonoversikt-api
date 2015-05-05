package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.json.JSONException;
import org.json.JSONWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections15.ListUtils.union;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class AnsattEnhetDropdown extends Select2Choice<AnsattEnhet> {

    public static final AnsattEnhet SKILLE_ENHET = new AnsattEnhet(null, null);
    public static final AnsattEnhet TOM_ENHET = new AnsattEnhet("", "");
    public static final List<AnsattEnhet> IKKE_VELGBARE_ENHETER = asList(SKILLE_ENHET, TOM_ENHET);

    public AnsattEnhetDropdown(String id, IModel<AnsattEnhet> model, List<AnsattEnhet> enheter, List<AnsattEnhet> foreslatteEnheter) {
        super(id, model, new AnsattEnhetChoiceProvider(enheter, foreslatteEnheter));
        getSettings().setContainerCssClass("enhetvalg");
        add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // nop
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getSettings().setPlaceholder(getString("ansattenhetdropdown.null"));
    }

    private static final class AnsattEnhetChoiceProvider extends DisableableTextChoiceProvider<AnsattEnhet> {

        private final List<AnsattEnhet> enheter, foreslatteEnheter;

        private AnsattEnhetChoiceProvider(List<AnsattEnhet> enheter, List<AnsattEnhet> foreslatteEnheter) {
            this.enheter = enheter;
            this.foreslatteEnheter = foreslatteEnheter;
        }

        @Override
        protected String getDisplayText(AnsattEnhet choice) {
            return choice.enhetId + " " + choice.enhetNavn;
        }

        @Override
        protected Object getId(AnsattEnhet choice) {
            return choice.enhetId;
        }

        @Override
        protected boolean isDisabled(AnsattEnhet choice) {
            return IKKE_VELGBARE_ENHETER.contains(choice);
        }

        @Override
        public void query(String term, int page, Response<AnsattEnhet> response) {
            List<AnsattEnhet> resultater = new ArrayList<>();
            for (AnsattEnhet enhet : union(foreslatteEnheter, enheter)) {
                if (containsIgnoreCase(enhet.enhetId, term) || containsIgnoreCase(enhet.enhetNavn, term) || IKKE_VELGBARE_ENHETER.contains(enhet)) {
                    resultater.add(enhet);
                }
            }

            // Ikke vis foreslåtte enheter når man søker, for å ikke vise duplikater og tomme elementer.
            if (resultater.size() < enheter.size()) {
                resultater.removeAll(foreslatteEnheter);
            }

            response.addAll(resultater);
        }

        @Override
        public Collection<AnsattEnhet> toChoices(Collection<String> ids) {
            for (AnsattEnhet enhet : union(foreslatteEnheter, enheter)) {
                if (ids.contains(enhet.enhetId)) {
                    return asList(enhet);
                }
            }
            throw new RuntimeException(format("Den valgte enheten med id %s finnes ikke.", ids.iterator().next()));
        }
    }

    abstract static class DisableableTextChoiceProvider<T> extends ChoiceProvider<T> {
        protected abstract String getDisplayText(T choice);

        protected abstract Object getId(T choice);

        protected abstract boolean isDisabled(T choice);

        @Override
        public final void toJson(T choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(getId(choice)).key("text").value(getDisplayText(choice)).key("disabled").value(isDisabled(choice));
        }
    }
}
