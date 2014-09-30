package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor.AktorEndpointConfig.AKTOER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.sakogbehandling.SakOgBehandlingEndpointConfig.SAKOGBEHANDLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakHentSakslisteEndpointConfig.GSAK_SAKSLISTE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.ruting.GsakRutingEndpointConfig.GSAK_RUTING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.journalforing.BehandleJournalV2EndpointConfig.BEHANDLE_JOURNAL_V2_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock.CMS_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock.ANTALLSAKER_PROPERTY;

public class MockSetupPage extends BasePage {

    private List<MockSetupModel> mockSetupModeller;
    private Model<String> antallSaker = new Model<>(getProperty(ANTALLSAKER_PROPERTY, "0"));

    public MockSetupPage() {
        mockSetupModeller = lagModeller();

        add(new ContextImage("modiaLogo", "img/modiaLogo.svg"));
        Form form = new Form("velgMockForm") {
            @Override
            protected void onSubmit() {
                for (MockSetupModel model : mockSetupModeller) {
                    setProperty(model.getKey(), model.getMockProperty());
                }
                setProperty(ANTALLSAKER_PROPERTY, antallSaker.getObject());
                setResponsePage(HentPersonPage.class);
            }
        };
        form.add(
                createMockCheckBoxer(),
                new TextField<>("antallSaker", antallSaker)
        );

        add(form);
    }

    private ListView<MockSetupModel> createMockCheckBoxer() {
        return new ListView<MockSetupModel>("radioliste", mockSetupModeller) {
            @Override
            protected void populateItem(ListItem<MockSetupModel> item) {
                item.add(
                        new Label("radiolabel", item.getModelObject().getServiceName()),
                        new CheckBox("mockvalg", new PropertyModel<Boolean>(item.getModelObject(), "useMock")).setOutputMarkupId(true)
                );
            }
        };
    }

    private List<MockSetupModel> lagModeller() {
        return asList(
                new MockSetupModel("Utbetaling", UTBETALING_KEY),
                new MockSetupModel("Kodeverk", KODEVERK_KEY),
                new MockSetupModel("Kjerneinfo", KJERNEINFO_KEY),
                new MockSetupModel("Henvendelse", HENVENDELSE_KEY),
                new MockSetupModel("Gsak", GSAK_V3_KEY),
                new MockSetupModel("Gsak saksliste", GSAK_SAKSLISTE_KEY),
                new MockSetupModel("Gsak ruting", GSAK_RUTING_KEY),
                new MockSetupModel("Journalføring", BEHANDLE_JOURNAL_V2_KEY),
                new MockSetupModel("SakOgBehandling", SAKOGBEHANDLING_KEY),
                new MockSetupModel("AktoerId", AKTOER_KEY),
                new MockSetupModel("HenvendelseSoknader", HENVENDELSESOKNADER_KEY),
                new MockSetupModel("NORG", NORG_KEY),
                new MockSetupModel("CMS", CMS_KEY)
        );
    }

}