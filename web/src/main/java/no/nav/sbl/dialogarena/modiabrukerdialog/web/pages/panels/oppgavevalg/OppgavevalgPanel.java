package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy.Synlighet.flippSynlighet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy.Synlighet.skjul;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy.Synlighet.skjulMenTaMedIMarkupLikevel;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy.Synlighet.taMedIMarkupSelvOmUsynlig;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy.Synlighet.vis;

public class OppgavevalgPanel extends Panel {

    private AjaxLink<Void> leggTilbakeLenke;
    private WebMarkupContainer valgliste;

    public OppgavevalgPanel(String id) {
        super(id);
        setOutputMarkupId(true);
        final LeggTilbakeForm leggTilbakeSkjema = new LeggTilbakeForm("legg-tilbake-form");
        instantiateLeggTilbakeLenke(leggTilbakeSkjema);
        instantiateValgliste(leggTilbakeSkjema);
        add(
                valgliste,
                leggTilbakeSkjema,
                createOppgavevalgLenke(leggTilbakeSkjema)
        );
    }

    private void instantiateLeggTilbakeLenke(LeggTilbakeForm leggTilbakeSkjema) {
        leggTilbakeLenke = createLeggTilbakeLink(leggTilbakeSkjema);
        taMedIMarkupSelvOmUsynlig(leggTilbakeLenke);
    }

    private void instantiateValgliste(LeggTilbakeForm leggTilbakeSkjema) {
        valgliste = (WebMarkupContainer) new WebMarkupContainer("oppgavevalg-liste").add(leggTilbakeLenke);
        skjulMenTaMedIMarkupLikevel(leggTilbakeSkjema, valgliste);
    }

    private AjaxLink<Void> createOppgavevalgLenke(final LeggTilbakeForm leggTilbakeSkjema) {
        return new AjaxLink<Void>("oppgavevalg-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                flippSynlighet(valgliste);
                skjul(leggTilbakeSkjema);
                target.add(valgliste, leggTilbakeSkjema);
            }
        };
    }

    private AjaxLink<Void> createLeggTilbakeLink(final LeggTilbakeForm leggTilbakeSkjema) {
        return new AjaxLink<Void>("legg-tilbake-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                skjul(valgliste);
                vis(leggTilbakeSkjema);
                target.add(valgliste, leggTilbakeSkjema);
            }
        };
    }
}
