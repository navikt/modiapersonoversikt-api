package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
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
    public void skalLageContainerForReactKomponenten() {
        wicket.goToPageWith(new ReactComponentPanel("id", "component"))
                .should().containComponent(ofType(WebMarkupContainer.class));
    }

    @Test
    public void javascriptInneholderRiktigeReferanserTilGlobaltScope() {
        String componentName = "component";
        ReactComponentPanel react = new ReactComponentPanel("id", componentName);
        IHeaderResponse headerResponse = spy(IHeaderResponse.class);
        ArgumentCaptor<OnDomReadyHeaderItem> headerItemCaptor = ArgumentCaptor.forClass(OnDomReadyHeaderItem.class);

        react.renderHead(headerResponse);

        verify(headerResponse, times(1)).render(headerItemCaptor.capture());
        OnDomReadyHeaderItem headerItem = headerItemCaptor.getValue();

        String javaScript = (String) headerItem.getJavaScript();

        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javaScript.contains(react.get("reactContainer").getMarkupId()));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_REACT));
        assertTrue(javaScript.contains(ReactComponentPanel.JS_REF_COMPONENTS + "." + componentName));
    }

    @Test
    public void updateStateLeggerKorrektJSPaTarget() {
        String componentName = "component";
        ReactComponentPanel react = new ReactComponentPanel("id", componentName);
        AjaxRequestTarget target = spy(AjaxRequestTarget.class);
        ArgumentCaptor<String> javascriptCaptor = ArgumentCaptor.forClass(String.class);

        Map<String, Object> props = new HashMap<String, Object>() {{

        }};

        react.updateState(target, props);

        verify(target, times(1)).appendJavaScript(javascriptCaptor.capture());
        String javascript = javascriptCaptor.getValue();

        assertTrue(javascript.contains(ReactComponentPanel.JS_REF_INITIALIZED_COMPONENTS));
        assertTrue(javascript.contains(react.get("reactContainer").getMarkupId()));
        assertTrue(javascript.contains("setState"));
    }

    static class TestPage extends Page {
    }
}