package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class AnsattEnhetDropdown extends Select2Choice<AnsattEnhet> {

    public AnsattEnhetDropdown(String id, IModel<AnsattEnhet> model, List<AnsattEnhet> enheter) {
        super(id, model, new AnsattEnhetChoiceProvider(enheter));

        getSettings().setContainerCssClass("enhetvalg");
    }

    private static final class AnsattEnhetChoiceProvider extends TextChoiceProvider<AnsattEnhet> {

        private final List<AnsattEnhet> enheter;

        private AnsattEnhetChoiceProvider(List<AnsattEnhet> enheter) {
            this.enheter = enheter;
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
        public void query(String term, int page, Response<AnsattEnhet> response) {
            List<AnsattEnhet> resultater = new ArrayList<>();
            for (AnsattEnhet enhet : enheter) {
                if (containsIgnoreCase(enhet.enhetId, term) || containsIgnoreCase(enhet.enhetNavn, term)) {
                    resultater.add(enhet);
                }
            }
            response.addAll(resultater);
        }

        @Override
        public Collection<AnsattEnhet> toChoices(Collection<String> ids) {
            for (AnsattEnhet enhet : enheter) {
                if (ids.contains(enhet.enhetId)) {
                    return asList(enhet);
                }
            }
            throw new RuntimeException(format("Den valgte enheten med id %s finnes ikke.", ids.iterator().next()));
        }
    }
}
