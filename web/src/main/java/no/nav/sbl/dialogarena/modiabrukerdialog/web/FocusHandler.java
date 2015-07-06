package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class FocusHandler extends TransparentWebMarkupContainer {
    public FocusHandler(String id) {
        super(id);
    }

    @RunOnEvents({
            SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT,
            LeggTilbakePanel.LEGG_TILBAKE_FERDIG,
            Events.SporsmalOgSvar.OPPGAVE_OPPRETTET_FERDIG
    })
    public void sakbehandlerValgteEnhet(AjaxRequestTarget target) {
        TextField input = ComponentFinder.in(getPage()).findWithId(TextField.class, "foedselsnummerInput");
        target.focusComponent(input);
    }

    @RunOnEvents(BasePage.SIDE_LASTET)
    public void personHarBlittSoktOpp(AjaxRequestTarget target) {
//        Venter på at visittkortet skal bli oppdatert
//        if (getPage().getClass() == PersonPage.class) {
//        Label label = ComponentFinder.in(getPage()).findWithId(Label.class, "personfakta.personnavn.fornavn");
//        target.focusComponent(label);
//        }
    }

    static final class ComponentFinder {
        private final Page page;

        private ComponentFinder(Page page) {
            this.page = page;
        }

        public static ComponentFinder in(Page page) {
            return new ComponentFinder(page);
        }

        public <T extends Component> T findWithId(Class<T> type, final String id) {
            T t = page.visitChildren(type, new IVisitor<T, T>() {
                @Override
                public void component(T component, IVisit<T> iVisit) {
                    if (component.getId().equals(id)) {
                        iVisit.stop(component);
                    }
                }
            });
            if (t == null) {
                throw new ApplicationException(format("Fant ikke focus-element av typen %s med id %s", type.getName(), id));
            }
            return t;
        }

        public <T extends Component> T find(Class<T> type) {
            final List<T> list = new LinkedList<>();
            page.visitChildren(type, new IVisitor<T, T>() {
                @Override
                public void component(T component, IVisit<T> iVisit) {
                    list.add(component);
                }
            });
            if (list.size() > 1 || list.isEmpty()) {
                throw new ApplicationException(format("Fant %d elementer som matchet %s. Forventet bare ett element", list.size(), type.getName()));
            }
            return list.get(0);
        }
    }
}
