package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;


public class FilterPanelTest extends AbstractWicketTest{

    private static final String DAGPENGER = "Dagpenger";
    private static final String ID = "id";

    private FilterParametere filterParametre;

    @Override
    protected void setup() {
        wicketTester.tester.getSession().replaceSession();

        Set<String> hovedYtelser = new HashSet<>(asList(DAGPENGER));
        filterParametre = new FilterParametere(hovedYtelser);
        FilterPanel formPanel = new FilterPanel("filterPanel", filterParametre);
        wicketTester.goToPageWith(formPanel);
    }

    @Test
    public void testFilterPanel() throws Exception {
        wicketTester.should().containComponent(ofType(FilterPanel.class))
                .should().inComponent(FilterPanel.class).containComponent(ofType(Form.class))
                .should().inComponent(Form.class).containComponents(4, ofType(AjaxCheckBox.class))
                .should().inComponent(Form.class).containComponent(withId("visBruker"))
                .should().inComponent(Form.class).containComponent(withId("visAnnenMottaker"))
                .should().inComponent(Form.class).containComponent(withId("visAlleYtelser"))
                .should().inComponent(Form.class).containComponent(withId("visYtelse"))
                .should().inComponent(Form.class).containComponent(ofType(DateRangePicker.class));
    }

}
