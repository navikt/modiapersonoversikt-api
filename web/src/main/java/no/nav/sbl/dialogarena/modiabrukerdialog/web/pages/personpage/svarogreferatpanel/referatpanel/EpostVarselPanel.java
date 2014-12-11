package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class EpostVarselPanel extends Panel {

    @Inject
    private BrukerprofilServiceBi brukerprofil;

    public EpostVarselPanel(String id, String fnr) {
        super(id);
        setVisibilityAllowed(!harEpost(fnr));
    }

    private boolean harEpost(String fnr) {
        try {
            BrukerprofilResponse response = brukerprofil.hentKontaktinformasjonOgPreferanser(new BrukerprofilRequest(fnr));
            return isNotBlank(response.getBruker().getEpostAdresse().getIdentifikator());
        } catch (Exception e) {
            return false;
        }
    }
}
