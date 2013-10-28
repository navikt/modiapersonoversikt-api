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
import org.slf4j.Logger;

import java.util.List;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

public class MockSetupPage extends BasePage {

    private static final Logger LOG = getLogger(MockSetupPage.class);

    private String selected = "";

    public MockSetupPage() {
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                new FeedbackPanel("feedback"),
                createVelgMockForm()
        );
    }

    private Form<Void> createVelgMockForm() {
        final String mockString = "Alt er mock";
        final List<String> alternativer = asList(mockString, "Alt er ekte tjenester");
        Form<Void> form = new Form<Void>("velgMockForm") {

            @Override
            protected void onSubmit() {
                ModiaApplicationContext context = (ModiaApplicationContext) WicketApplication.get().getApplicationContext();
                boolean mockAlt = mockString.equals(selected);
                LOG.debug("mockAlt = " + mockAlt);
                if(context != null) {
                    context.doRefresh(mockAlt);

                    PageParameters parameters = new PageParameters();
                    if (mockAlt) {
                        parameters.add("fnr", "23067911223");
                    }
                    getRequestCycle().setResponsePage(Intern.class, parameters);
                } else {
                    error("Context kunne ikke castes til ModiaApplicationContext");
                }
            }
        };
        return (Form<Void>) form.add(new RadioChoice<>("velgMock", new PropertyModel<String>(this, "selected"), alternativer));
    }
}
