package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;

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
        final List<String> alternativer = asList("Ja", "Nei");

        Form<Void> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                List<MockSetupModel> models = listView.getModelObject();
                for (MockSetupModel model : models) {
                    setProperty(model.getKey(), model.getMockProperty());
                }
                PageParameters parameters = new PageParameters();
                parameters.add("fnr", "23067911223");
                getRequestCycle().setResponsePage(Intern.class, parameters);
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
