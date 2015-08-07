package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class AjaxLazyLoadVelgSakPanel extends AnimertPanel {

    public static final String LAZY_LOAD_COMPONENT_ID = "content";

    public enum State {
        INITIALIZE, LOADING, FAILED, LOADED
    }

    private State state = State.INITIALIZE;
    private String fokusEtterLukking;

    public AjaxLazyLoadVelgSakPanel(String id, final String fnr, final IModel<HenvendelseVM> henvendelseVM, final boolean visSosialeTjenester) {
        super(id);
        setOutputMarkupId(true);
        add(getLoadingComponent(LAZY_LOAD_COMPONENT_ID));

        add(new Ajax() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if (state == State.INITIALIZE) {
                    AjaxLazyLoadVelgSakPanel.this.replace(getLoadingComponent(LAZY_LOAD_COMPONENT_ID));
                    state = State.LOADING;
                    target.add(AjaxLazyLoadVelgSakPanel.this.get(LAZY_LOAD_COMPONENT_ID));
                }
            }
        }, new Ajax() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if (state == State.LOADING) {
                    VelgSakPanel velgSakPanel = new VelgSakPanel(LAZY_LOAD_COMPONENT_ID, fnr, henvendelseVM, visSosialeTjenester);
                    velgSakPanel.oppdaterSaker();
                    velgSakPanel.settFokusEtterLukking(fokusEtterLukking);
                    velgSakPanel.setOutputMarkupId(true);
                    AjaxLazyLoadVelgSakPanel.this.replace(velgSakPanel);
                    state = State.LOADED;
                    target.add(AjaxLazyLoadVelgSakPanel.this.get(LAZY_LOAD_COMPONENT_ID));
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

    public void settFokusEtterLukking(String markupId) {
        this.fokusEtterLukking = markupId;
    }

    private abstract static class Ajax extends AbstractDefaultAjaxBehavior {
        @Override
        public void renderHead(final Component component, final IHeaderResponse response) {
            super.renderHead(component, response);
            CharSequence js = getCallbackScript(component);
            handleCallbackScript(response, js);
        }

        private void handleCallbackScript(final IHeaderResponse response,
                                          final CharSequence callbackScript) {
            response.render(OnDomReadyHeaderItem.forScript(callbackScript));
        }

    }
}
