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


public class FilterFormPanelTest extends AbstractWicketTest{

    @Override
    protected void setup() {
        wicketTester.tester.getSession().replaceSession();
    }

    @Test
    public void testFilterPanel() throws Exception {
        Set<String> hovedYtelser = new HashSet<>(asList("Mat"));
        FilterParametere filterParametre = new FilterParametere(hovedYtelser);
        FilterFormPanel filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametre);
        wicketTester.goToPageWith(filterFormPanel);

        wicketTester.should().containComponent(ofType(FilterFormPanel.class))
                .should().inComponent(FilterFormPanel.class).containComponent(ofType(Form.class))
                .should().inComponent(Form.class).containComponents(4, ofType(AjaxCheckBox.class))
                .should().inComponent(Form.class).containComponent(withId("visBruker"))
                .should().inComponent(Form.class).containComponent(withId("visAnnenMottaker"))
                .should().inComponent(Form.class).containComponent(withId("visAlleYtelser"))
                .should().inComponent(Form.class).containComponent(withId("visYtelse"))
                .should().inComponent(Form.class).containComponent(ofType(DateRangePicker.class));
    }
}
