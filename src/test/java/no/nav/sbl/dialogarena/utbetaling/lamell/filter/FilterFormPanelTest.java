package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class FilterFormPanelTest extends AbstractWicketTest{

    private static final String DAGPENGER = "Dagpenger";
    private static final String ID = "id";

    private FilterParametere filterParametre;

    @Override
    protected void setup() {
        wicketTester.tester.getSession().replaceSession();

        Set<String> hovedYtelser = new HashSet<>(asList(DAGPENGER));
        filterParametre = new FilterParametere(hovedYtelser);
        FilterFormPanel filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametre);
        wicketTester.goToPageWith(filterFormPanel);
    }

    @Test
    public void testFilterPanel() throws Exception {
        wicketTester.should().containComponent(ofType(FilterFormPanel.class))
                .should().inComponent(FilterFormPanel.class).containComponent(ofType(Form.class))
                .should().inComponent(Form.class).containComponents(4, ofType(AjaxCheckBox.class))
                .should().inComponent(Form.class).containComponent(withId("visBruker"))
                .should().inComponent(Form.class).containComponent(withId("visAnnenMottaker"))
                .should().inComponent(Form.class).containComponent(withId("visAlleYtelser"))
                .should().inComponent(Form.class).containComponent(withId("visYtelse"))
                .should().inComponent(Form.class).containComponent(ofType(DateRangePicker.class));
    }

    @Test
    public void testYtelseFilterBryter() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, DAGPENGER)
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER)
                .with(Hovedytelse.posteringsdato, now());

        assertTrue(filterParametre.evaluate(ytelse));

        utbetalingEvaluererTrueOmYtelsenBlirValgt(ytelse);
        utbetalingEvaluererFalseOmYtelseIkkeErValgt(ytelse);
        utbetalingEvaluererTrueOmYtelsenBlirValgt(ytelse);
    }

    private void utbetalingEvaluererTrueOmYtelsenBlirValgt(Record<Hovedytelse> ytelse) {
        wicketTester.click().ajaxCheckbox(withId("visYtelse"));
        assertTrue(filterParametre.evaluate(ytelse));
    }

    private void utbetalingEvaluererFalseOmYtelseIkkeErValgt(Record<Hovedytelse> ytelse) {
        wicketTester.click().ajaxCheckbox(withId("visYtelse"));
        assertFalse(filterParametre.evaluate(ytelse));
    }
}
