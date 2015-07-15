package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class ReactComponentPanelTest {

    private FluentWicketTester<?> wicket = new FluentWicketTester<>(new WebApplication() {
        @Override
        public Class<? extends Page> getHomePage() {
            return TestPage.class;
        }
    });

    @Before
    public void setup() {
        wicket.tester.getSession().replaceSession();
    }

    @Test
    public void javascriptInneholderRiktigeReferanserTilGlobaltScope() {
        String componentName = "component";
        ReactComponentPanel react = new ReactComponentPanel("id", componentName);

        String javaScript = react.initializeScript(componentName, new HashMap<String, Object>());

        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_REACT));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_COMPONENTS + "." + componentName));
    }

    @Test
    public void updateStateLeggerKorrektJSPaTarget() {
        String componentName = "component";
        ReactComponentPanel react = new ReactComponentPanel("id", componentName);

        Map<String, Object> props = new HashMap<String, Object>() {{

        }};

        String javascript = react.callScript("setState", react.prepareArguments(props));


        assertTrue(javascript.contains(ReactComponentPanel.JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javascript.contains("setState"));
    }

    static class TestPage extends Page {
    }
}