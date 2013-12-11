package no.nav.sbl.dialogarena.utbetaling.lamell;

import static no.nav.sbl.dialogarena.utbetaling.domain.Periode.intervall;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;
import static no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere.ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere.HOVEDYTELSER_ENDRET;

import java.util.List;

import javax.inject.Inject;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsResultat;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.LocalDate;

public class UtbetalingLerret extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");

    @Inject
    private String arenaUtbetalingUrl;

    @Inject
    private UtbetalingService service;

    private UtbetalingsResultat resultatCache;
    private FilterParametere filterParametere;
    private FilterFormPanel filterFormPanel;
    private OppsummeringPanel totalOppsummeringPanel;
    private MarkupContainer utbetalingslisteContainer;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                new ExternalLink("arenalink", arenaUtbetalingUrl + fnr),
                filterFormPanel,
                totalOppsummeringPanel.setOutputMarkupPlaceholderTag(true),
                utbetalingslisteContainer.setOutputMarkupId(true)
        );
    }

    private void instansierFelter(String fnr) {
    	LocalDate startDato = defaultStartDato();
		LocalDate sluttDato = defaultSluttDato();
		List<Utbetaling> utbetalinger = service.hentUtbetalinger(fnr, startDato, sluttDato);
    	resultatCache = new UtbetalingsResultat(fnr, startDato, sluttDato, utbetalinger);

        filterParametere = new FilterParametere(startDato, sluttDato, true, true, hentYtelser(utbetalinger));
        totalOppsummeringPanel = createTotalOppsummeringPanel(resultatCache.getSynligeUtbetalinger(filterParametere));
        utbetalingslisteContainer = new WebMarkupContainer("utbetalingslisteContainer").add(opprettMaanedsPanelListe(resultatCache, filterParametere));

        filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametere);
        filterFormPanel.setOutputMarkupId(true);
    }

    @RunOnEvents(ENDRET)
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
    	oppdaterCacheOmNodvendig();
        List<Utbetaling> alleUtbetalinger = resultatCache.hentUtbetalinger(filterParametere.getStartDato(), filterParametere.getSluttDato());
        filterParametere.setYtelser(hentYtelser(alleUtbetalinger));
        sendYtelserEndretEvent();

        List<Utbetaling> synligeUtbetalinger = resultatCache.getSynligeUtbetalinger(filterParametere);
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filterParametere.getStartDato(),
                filterParametere.getSluttDato()));
        totalOppsummeringPanel.setVisibilityAllowed(synligeUtbetalinger.size() > 1);
        utbetalingslisteContainer.addOrReplace(opprettMaanedsPanelListe(resultatCache, filterParametere));
        target.add(totalOppsummeringPanel, utbetalingslisteContainer);
    }

	private void oppdaterCacheOmNodvendig() {
		if (!intervall(resultatCache.startDato, resultatCache.sluttDato).contains(intervall(filterParametere.getStartDato(), filterParametere.getSluttDato()))) {
			List<Utbetaling> utbetalinger = service.hentUtbetalinger(resultatCache.fnr, filterParametere.getStartDato(), filterParametere.getSluttDato());
	    	resultatCache = new UtbetalingsResultat(resultatCache.fnr, filterParametere.getStartDato(), filterParametere.getSluttDato(), utbetalinger);
		}
	}

    private OppsummeringPanel createTotalOppsummeringPanel(List<Utbetaling> liste) {
        return new OppsummeringPanel("totalOppsummeringPanel",
                new CompoundPropertyModel<>(new OppsummeringProperties(liste, filterParametere.getStartDato(), filterParametere.getSluttDato())), true);
    }

    private static Component opprettMaanedsPanelListe(UtbetalingsResultat resultat, final FilterParametere filter) {
        List<List<Utbetaling>> maanedsListe = resultat.hentFiltrertUtbetalingerPerMaaned(filter);
        return new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject(), filter));
            }
        }.setOutputMarkupId(true);
    }

    @RunOnEvents(FilterFormPanel.FEIL)
    private void skjulSnurrepippVedFeil(AjaxRequestTarget target) {
        target.add(totalOppsummeringPanel);
    }

    private void sendYtelserEndretEvent() {
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

}
