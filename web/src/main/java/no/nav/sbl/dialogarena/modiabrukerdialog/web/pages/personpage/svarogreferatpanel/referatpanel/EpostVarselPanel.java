package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.HenvendelseVM;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class EpostVarselPanel extends Panel {

    @Inject
    private BrukerprofilServiceBi brukerprofil;

    public EpostVarselPanel(String id, IModel<HenvendelseVM.Modus> modusModel, String fnr) {
        super(id);
        add(visibleIf(
                both(isEqualTo(modusModel, HenvendelseVM.Modus.SPORSMAL))
                        .and(not(harEpost(fnr)))));
    }

    private IModel<Boolean> harEpost(final String fnr) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                try {
                    BrukerprofilResponse response = brukerprofil.hentKontaktinformasjonOgPreferanser(new BrukerprofilRequest(fnr));
                    return isNotBlank(response.getBruker().getEpostAdresse().getIdentifikator());
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }
}