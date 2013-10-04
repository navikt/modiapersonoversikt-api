package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
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

    private final AjaxLink<Void> leggTilbakeLenke;
    private final WebMarkupContainer valgliste;

    public OppgavevalgPanel(String id) {
        super(id);

        final LeggTilbakeForm leggTilbakeSkjema = new LeggTilbakeForm("legg-tilbake-form");

        valgliste = new WebMarkupContainer("oppgavevalg-liste");

        leggTilbakeLenke = new AjaxLink<Void>("legg-tilbake-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                skjul(valgliste);
                vis(leggTilbakeSkjema);
                target.add(valgliste, leggTilbakeSkjema);
            }
        };
        taMedIMarkupSelvOmUsynlig(leggTilbakeLenke);

        skjulMenTaMedIMarkupLikevel(leggTilbakeSkjema, valgliste);

        valgliste.add(leggTilbakeLenke);

        AjaxLink<Void> oppgavevalglenke = new AjaxLink<Void>("oppgavevalg-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                flippSynlighet(valgliste);
                skjul(leggTilbakeSkjema);
                target.add(valgliste, leggTilbakeSkjema);
            }
        };

        add(valgliste, leggTilbakeSkjema, oppgavevalglenke);
    }

    @RunOnEvents(Modus.KVITTERING)
    private void kvitteringsmodus(AjaxRequestTarget target) {
        skjul(leggTilbakeLenke);
        if (target != null) {
            target.add(valgliste);
        }
    }
}
