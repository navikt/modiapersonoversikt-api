package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;

public class AnimertJournalforingsPanel extends AnimertPanel {

    public static final String TRAAD_JOURNALFORT = "sos.journalforingspanel.traadJournalfort";
    public static final String LAZY_LOAD_COMPONENT_ID = "content";

    public enum State {
        INITIALIZE, LOADING, FAILED, LOADED
    }

    private State state = State.INITIALIZE;

    public AnimertJournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);
        add(getLoadingComponent(LAZY_LOAD_COMPONENT_ID));

        add(new Ajax() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if (state == State.INITIALIZE) {
                    AnimertJournalforingsPanel.this.replace(getLoadingComponent(LAZY_LOAD_COMPONENT_ID));
                    state = State.LOADING;
                    target.add(AnimertJournalforingsPanel.this.get(LAZY_LOAD_COMPONENT_ID));
                }
            }
        }, new Ajax() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if (state == State.LOADING) {
                    JournalforingsPanel journalforingsPanel = new JournalforingsPanel(LAZY_LOAD_COMPONENT_ID, innboksVM);
                    journalforingsPanel.oppdatereJournalforingssaker();
                    journalforingsPanel.setOutputMarkupId(true);
                    AnimertJournalforingsPanel.this.replace(journalforingsPanel);
                    state = State.LOADED;
                    target.add(AnimertJournalforingsPanel.this.get(LAZY_LOAD_COMPONENT_ID));
                }
            }
        });
    }

    private Component getLoadingComponent(final String markupId) {
        return new Label(markupId, "<img alt=\"Loading...\" src=\"" + imageUrl() + "\"/>")
                .setEscapeModelStrings(false)
                .setOutputMarkupId(true);
    }

    protected String imageUrl() {
        return "/modiabrukerdialog/img/ajaxloader/svart/loader_svart_64.gif";
    }

    @Override
    public void togglePanel(AjaxRequestTarget target) {
        state = State.INITIALIZE;
        super.togglePanel(target);
    }

    @RunOnEvents(TRAAD_JOURNALFORT)
    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
    }

    private static abstract class Ajax extends AbstractDefaultAjaxBehavior {
        @Override
        public void renderHead(final Component component, final IHeaderResponse response) {
            super.renderHead(component, response);
            CharSequence js = getCallbackScript(component);
            handleCallbackScript(response, js, component);
        }

        private void handleCallbackScript(final IHeaderResponse response,
                                          final CharSequence callbackScript, final Component component) {
            response.render(OnDomReadyHeaderItem.forScript(callbackScript));
        }

    }
}
