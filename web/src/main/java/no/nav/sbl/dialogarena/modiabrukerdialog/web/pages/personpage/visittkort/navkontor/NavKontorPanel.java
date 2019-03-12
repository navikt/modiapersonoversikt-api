package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.visittkort.navkontor;

import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.AbstractKjerneInfoPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;

import java.util.HashMap;
import java.util.Optional;

public class NavKontorPanel extends AbstractKjerneInfoPanel {

	public static final String BRUKERS_NAV_ENHET_WICKET_ID = "reactBrukersNavKontorContainer";
	public static final String BRUKERS_NAV_KONTOR_REACT_MODULE_ID = "BrukersNavKontor";
    public static final String NORG2_FRONTEND_BASE_URL_PROPERTY_KEY = "server.norg2-frontend.url";

    public NavKontorPanel(String id, String fnr) {
		super(id, fnr);
        add(lagBrukersNavEnhetPanel());
	}

	private ReactComponentPanel lagBrukersNavEnhetPanel() {
		return new ReactComponentPanel(BRUKERS_NAV_ENHET_WICKET_ID, BRUKERS_NAV_KONTOR_REACT_MODULE_ID, getNavKontorProps());
	}

    private HashMap<String, Object> getNavKontorProps() {
        HashMap<String, Object> props = new HashMap<>();
        props.put("norg2FrontendBaseUrl", System.getProperty(NORG2_FRONTEND_BASE_URL_PROPERTY_KEY));
        String navKontorID = getAnsvarligEnhetId(personModel.getObject().getPersonfakta()).orElse("");
        props.put("organisasjonsenhetId", navKontorID);
        return props;
    }

    private Optional<String> getAnsvarligEnhetId(Personfakta personfakta) {
		return Optional.ofNullable(personfakta.getAnsvarligEnhet())
				.map(AnsvarligEnhet::getOrganisasjonsenhet)
				.map(Organisasjonsenhet::getOrganisasjonselementId);
	}
}
