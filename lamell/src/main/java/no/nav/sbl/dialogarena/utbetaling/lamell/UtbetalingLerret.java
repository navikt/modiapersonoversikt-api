package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsHolder;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;

public class UtbetalingLerret extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");

    @Inject
    private UtbetalingsHolder utbetalingsHolder;

    @Inject
    private String arenaUtbetalingUrl;

    private FilterParametere filterParametere;
    private OppsummeringPanel totalOppsummeringPanel;
    private MarkupContainer utbetalingslisteContainer;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                new ExternalLink("arenalink", arenaUtbetalingUrl + fnr),
                new FilterFormPanel("filterFormPanel", filterParametere),
                totalOppsummeringPanel.setOutputMarkupPlaceholderTag(true),
                utbetalingslisteContainer.setOutputMarkupId(true)
        );
    }

    private void instansierFelter(String fnr) {
        filterParametere = new FilterParametere(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);
        totalOppsummeringPanel = createTotalOppsummeringPanel(utbetalingsHolder.withFnr(fnr).getSynligeUtbetalinger(filterParametere));
        utbetalingslisteContainer = new WebMarkupContainer("utbetalingslisteContainer").add(opprettMaanedsPanelListe());
    }

    private OppsummeringPanel createTotalOppsummeringPanel(List<Utbetaling> liste) {
        return new OppsummeringPanel("totalOppsummeringPanel",
                new CompoundPropertyModel<>(new OppsummeringProperties(liste, filterParametere.getStartDato(), filterParametere.getSluttDato())), true);
    }

    private Component opprettMaanedsPanelListe() {
        List<List<Utbetaling>> maanedsListe = utbetalingsHolder.getResultat().hentFiltrertUtbetalingerPerMaaned(filterParametere);
        return new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject(), filterParametere));
            }
        }.setOutputMarkupId(true);
    }

    @RunOnEvents(FilterParametere.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        List<Utbetaling> synligeUtbetalinger = utbetalingsHolder.getResultat().getSynligeUtbetalinger(filterParametere);
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filterParametere.getStartDato(),
                filterParametere.getSluttDato()));
        totalOppsummeringPanel.setVisibilityAllowed(synligeUtbetalinger.size() > 1);
        utbetalingslisteContainer.addOrReplace(opprettMaanedsPanelListe());
        target.add(totalOppsummeringPanel, utbetalingslisteContainer);
    }

    @RunOnEvents(FilterFormPanel.FEIL)
    @SuppressWarnings("unused")
    private void skjulSnurrepippVedFeil(AjaxRequestTarget target) {
        target.add(utbetalingslisteContainer);
    }
}
