package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.ModiaApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Arrays;
import java.util.List;

public class MockSetupPage extends BasePage {

    private String selected = "";

    public MockSetupPage() {

        final String mockStr = "Alt er mock";
        final String ekte = "Alt er ekte tjenester";
        final List<String> alternativer = Arrays.asList(new String[]{mockStr, ekte});
        RadioChoice<String> radioChoice = new RadioChoice<>("velgMock", new PropertyModel<String>(this, "selected"), alternativer);

        Form<?> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                ModiaApplicationContext context = (ModiaApplicationContext) WicketApplication.get().getApplicationContext();
                boolean mockAlt = mockStr.equals(selected);
                System.out.println("mockAlt = " + mockAlt);
                context.doRefresh(mockAlt);

                PageParameters parameters = new PageParameters();
                if(!mockAlt) {
                    parameters.add("fnr", "23067911223");
                }
                getRequestCycle().setResponsePage(Intern.class, parameters);
            }
        };

        add(new ContextImage("modia-logo", "img/modiaLogo.svg"));
        add(new FeedbackPanel("feedback"));
        form.add(radioChoice);
        add(form);
    }
}
