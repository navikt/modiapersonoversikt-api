package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;


public class FilterFormPanelTest extends AbstractWicketTest{

    @Override
    protected void setup() {
        wicketTester.tester.getSession().replaceSession();
    }

    @Test
    public void testFilterPanel() throws Exception {
        Map<String, Boolean> mottakere = new HashMap<>();
        mottakere.put(BRUKER, true);
        Set<String> hovedYtelser = new HashSet<>(asList("Mat"));
        FilterParametere filterParametre = new FilterParametere(defaultStartDato(), defaultSluttDato(), mottakere, hovedYtelser);
        FilterFormPanel filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametre);
        wicketTester.goToPageWith(filterFormPanel);

        wicketTester.should().containComponent(ofType(FilterFormPanel.class))
                .should().inComponent(FilterFormPanel.class).containComponent(ofType(Form.class))
                .should().inComponent(Form.class).containComponents(3, ofType(AjaxButton.class))
                .should().inComponent(Form.class).containComponent(withId("visBruker"))
                .should().inComponent(Form.class).containComponent(withId("visArbeidsgiver"))
                .should().inComponent(Form.class).containComponent(withId("ytelseKnapp"))
                .should().inComponent(Form.class).containComponent(ofType(DateRangePicker.class));
    }
}
