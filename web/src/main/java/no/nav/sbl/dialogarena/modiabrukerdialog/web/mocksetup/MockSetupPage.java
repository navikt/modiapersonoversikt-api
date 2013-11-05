package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class MockSetupPage extends BasePage {

    private ListView<MockSetupModel> listView;

    public MockSetupPage() {
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                new FeedbackPanel("feedback"),
                createVelgMockForm()
        );
    }

    private Form<Void> createVelgMockForm() {
        Form<Void> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                List<MockSetupModel> models = listView.getModelObject();
                String infostr = "";
                for (MockSetupModel model : models) {
                    setProperty(model.getKey(), model.getMockProperty());
                    infostr += model.getServiceName() + ": " + getProperty(model.getKey()) + ", ";
                }
                info(infostr);

                getRequestCycle().setResponsePage(HentPersonPage.class);
            }
        };
        listView = leggTilCheckBoxer();
        form.add(listView);
        return form;
    }

    private ListView<MockSetupModel> leggTilCheckBoxer() {
        List<MockSetupModel> models = lagModeller();

        return new ListView<MockSetupModel>("radioliste", models) {
            @Override
            protected void populateItem(ListItem item) {
                MockSetupModel model = (MockSetupModel) item.getModelObject();
                Label label = new Label("radiolabel", model.getServiceName());
                CheckBox mockvalg = new CheckBox("mockvalg", new PropertyModel<Boolean>(model, "useMock"));

                item.add(label);
                item.add(mockvalg);
            }
        };
    }

    private List<MockSetupModel> lagModeller() {
        ArrayList<MockSetupModel> models = new ArrayList<>();

        models.add(new MockSetupModel("8", "Kjerneinfo", "start.kjerneinfo.withmock"));
        models.add(new MockSetupModel("1", "Sak og behandling", "start.sakogbehandling.withmock"));
        models.add(new MockSetupModel("2", "AktørID", "start.aktor.withmock"));
        models.add(new MockSetupModel("3", "Utbetaling", "start.utbetaling.withmock"));
        models.add(new MockSetupModel("4", "Kodeverk", "start.kodeverk.withmock"));
        models.add(new MockSetupModel("5", "Henvendelse", "start.henvendelsemeldinger.withmock"));
        models.add(new MockSetupModel("6", "Besvare henvendelse", "start.besvarehenvendelse.withmock"));
        models.add(new MockSetupModel("7", "Oppgavebehandling", "start.oppgavebehandling.withmock"));
        return models;
    }
}
