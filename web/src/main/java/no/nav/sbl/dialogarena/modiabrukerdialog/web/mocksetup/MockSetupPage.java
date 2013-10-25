package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.List;

public class MockSetupPage extends BasePage {

    private String selected = "Alt er ekte tjenester";

    public MockSetupPage() {

        final List<String> alternativer = Arrays.asList(new String[]{"Alt er mock", "Alt er ekte tjenester"});
        RadioChoice<String> radioChoice = new RadioChoice<>("velgMock", new PropertyModel<String>(this, "selected"), alternativer);

        Form<?> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                info("Velgmock: " + selected);
            }
        };


        add(new ContextImage("modia-logo", "img/modiaLogo.svg"));
        add(new FeedbackPanel("feedback"));
        form.add(radioChoice);
        add(form);
    }
}
