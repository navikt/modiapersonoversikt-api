package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;


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
        FilterParametere filterParametre = new FilterParametere(Utbetaling.defaultStartDato(), Utbetaling.defaultSluttDato(), mottakere, hovedYtelser);
        FilterFormPanel filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametre, new FeedbackPanel("test"));
        wicketTester.goToPageWith(filterFormPanel);
//        wicketTester.printComponentsTree();
    }
}
