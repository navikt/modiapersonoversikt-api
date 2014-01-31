package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.FODSELSNUMMER;

public class MockSetupPage extends BasePage {

    private ListView<MockSetupModel> listView;
    private Boolean brukTestPerson = false;

    public MockSetupPage() {
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                new FeedbackPanel("feedback"),
                createVelgMockForm()
        );
    }

    @SuppressWarnings("unchecked")
    private Form<Void> createVelgMockForm() {
        listView = leggTilMockCheckBoxer();
        return (Form<Void>) new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                List<MockSetupModel> models = listView.getModelObject();
                StringBuilder mocks = new StringBuilder();
                for (MockSetupModel model : models) {
                    setProperty(model.getKey(), model.getMockProperty());
                    mocks.append(model.getServiceName()).append(": ").append(getProperty(model.getKey())).append(", ");
                }
                info(mocks.toString());
                redirect();
            }

            private void redirect() {
                if (brukTestPerson) {
                    getRequestCycle().setResponsePage(PersonPage.class, new PageParameters().add("fnr", FODSELSNUMMER));
                    return;
                }
                getRequestCycle().setResponsePage(HentPersonPage.class);
            }
        }.add(
                listView,
                new CheckBox("brukTestPerson", new PropertyModel<Boolean>(this, "brukTestPerson")
        ));
    }

    private ListView<MockSetupModel> leggTilMockCheckBoxer() {
        return new ListView<MockSetupModel>("radioliste", lagModeller()) {
            @Override
            protected void populateItem(ListItem item) {
                item.add(
                        new Label("radiolabel", ((MockSetupModel) item.getModelObject()).getServiceName()),
                        new CheckBox("mockvalg", new PropertyModel<Boolean>(item.getModelObject(), "useMock"))
                );
            }
        };
    }

    private List<MockSetupModel> lagModeller() {
        return asList(
                new MockSetupModel("1", "Utbetaling", UTBETALING_KEY),
                new MockSetupModel("2", "Kodeverk", KODEVERK_KEY),
                new MockSetupModel("3", "Kjerneinfo", KJERNEINFO_KEY)
        );
    }

}
