package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

public class MockSetupPage extends BasePage {

    private static final Logger LOG = getLogger(MockSetupPage.class);
    private String selected = "";
    private ListView<MockSetupModel> listView;

    public MockSetupPage() {
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                new FeedbackPanel("feedback"),
                createVelgMockForm()
        );
    }

    private Form<Void> createVelgMockForm() {
        final List<String> alternativer = asList("Ja", "Nei");

        Form<Void> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                List<MockSetupModel> modelObject = listView.getModelObject();
                System.out.println("modelObject = " + modelObject);
                info(modelObject.get(0).getServiceName() + " = " + modelObject.get(0).getUseMock());

//                PageParameters parameters = new PageParameters();
//                parameters.add("fnr", "23067911223");
//                getRequestCycle().setResponsePage(Intern.class, parameters);
            }
        };
        listView = leggTilRadioknapper(alternativer);
        form.add(listView);
        return form;
    }

    private ListView<MockSetupModel> leggTilRadioknapper(final List<String> alternativer) {
        List<MockSetupModel> models = lagModeller();

        ListView<MockSetupModel> listView = new ListView("radioliste", models) {
            @Override
            protected void populateItem(ListItem item) {
                MockSetupModel model = (MockSetupModel) item.getModelObject();
                Label label = new Label("radiolabel", model.getServiceName());
                RadioChoice<String> radioChoice = new RadioChoice<>("radiovalg", new PropertyModel<String>(model, "useMock"), alternativer);
                item.add(label);
                item.add(radioChoice);
            }
        };

        return listView;
    }

    private List<MockSetupModel> lagModeller() {
        ArrayList<MockSetupModel> models = new ArrayList<>();

        String defaultValue = "Nei";
        models.add(new MockSetupModel("1", "Sak og behandling", defaultValue));
        models.add(new MockSetupModel("2", "AktørID", defaultValue));
        models.add(new MockSetupModel("3", "Utbetaling", defaultValue));
        models.add(new MockSetupModel("4", "Kodeverk", defaultValue));
        models.add(new MockSetupModel("5", "Henvendelse", defaultValue));
        models.add(new MockSetupModel("6", "Besvare henvendelse", defaultValue));
        models.add(new MockSetupModel("6", "Oppgavebehandling", defaultValue));
        return models;
    }
}
