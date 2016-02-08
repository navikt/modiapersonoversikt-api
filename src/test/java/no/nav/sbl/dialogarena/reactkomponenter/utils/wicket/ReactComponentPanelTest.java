package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel.*;
import static org.junit.Assert.assertTrue;


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
        react.mapper = new ObjectMapper();

        String javaScript = react.initializeScript(componentName, new HashMap<>());

        assertTrue(javaScript.contains(JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_REACT));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_COMPONENTS + "." + componentName));
    }

    @Test
    public void updateStateLeggerKorrektJSPaTarget() {
        String componentName = "component";
        ReactComponentPanel react = new ReactComponentPanel("id", componentName);
        react.mapper = new ObjectMapper();

        Map<String, Object> props = new HashMap<String, Object>() {{

        }};

        String javascript = react.callScript("setState", react.prepareArguments(props));


        assertTrue(javascript.contains(JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javascript.contains("setState"));
    }

    @Test
    public void createScriptErGyldig() throws Exception {
        String componentName = "component";
        String wicketid = "wicketid";
        ReactComponentPanel panel = new ReactComponentPanel(wicketid, componentName);
        panel.mapper = new ObjectMapper();

        String script = panel.createScript(componentName, new HashMap<>());

        assertTrue(fuzzyMatch(
                script,
                JS_REF_INITIALIZED_COMPONENTS, ".", wicketid,
                " = ",
                JS_REF_REACT, "createElement(",
                JS_REF_COMPONENTS, componentName, "{}",
                ");"
        ));
    }

    @Test
    public void callScriptErGyldig() throws Exception {
        String componentName = "component";
        String methodName = "method";
        String wicketid = "wicketid";
        ReactComponentPanel panel = new ReactComponentPanel(wicketid, componentName);
        panel.mapper = new ObjectMapper();

        String script = panel.callScript(methodName, "{}");

        assertTrue(fuzzyMatch(
                script,
                JS_REF_INITIALIZED_COMPONENTS, ".", wicketid, ".", methodName, "({});"
        ));
    }

    @Test
    public void renderScriptErGyldig() throws Exception {
        String componentName = "component";
        String methodName = "method";
        String wicketid = "wicketid";
        ReactComponentPanel panel = new ReactComponentPanel(wicketid, componentName);
        panel.mapper = new ObjectMapper();

        String script = panel.renderScript();

        assertTrue(fuzzyMatch(
                script,
                JS_REF_INITIALIZED_COMPONENTS, ".", wicketid,
                " = ",
                JS_REF_REACT, "render(", JS_REF_INITIALIZED_COMPONENTS, wicketid,
                "document.getElementById", "'", wicketid,
                "'));"
        ));
    }


    static class TestPage extends Page {
    }

    static boolean fuzzyMatch(String actual, String... terms) {
        int fromIndex = 0;
        for (String term : terms) {
            int i = actual.indexOf(term, fromIndex);
            if (i < 0) {
                return false;
            }
            fromIndex = i + term.length();
        }
        return true;
    }
}