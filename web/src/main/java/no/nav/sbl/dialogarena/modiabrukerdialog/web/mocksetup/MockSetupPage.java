package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakOppgaveV2EndpointConfig.GSAK_V2_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_V2_KEY;

public class MockSetupPage extends BasePage {

    private List<MockSetupModel> mockSetupModeller;

    public MockSetupPage() {
        mockSetupModeller = lagModeller();

        add(new ContextImage("modia-logo", "img/modiaLogo.svg"));
        add(new Form("velgMockForm") {
            @Override
            protected void onSubmit() {
                for (MockSetupModel model : mockSetupModeller) {
                    setProperty(model.getKey(), model.getMockProperty());
                }
                setResponsePage(HentPersonPage.class);
            }
        }.add(createMockCheckBoxer()));
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
                new MockSetupModel("Henvendelse", HENVENDELSE_V2_KEY),
                new MockSetupModel("Gsak", GSAK_V2_KEY)
        );
    }

}
