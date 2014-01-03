package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned.MaanedsPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsResultat;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FEIL;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;

public class UtbetalingLerret extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    public static final JavaScriptResourceReference UTBETALING_LAMELL_JS = new JavaScriptResourceReference(UtbetalingLerret.class, "utbetaling.js");

    @Inject
    private String arenaUtbetalingUrl;
    @Inject
    private UtbetalingService service;

    private UtbetalingsResultat resultatCache;
    private FilterParametere filterParametere;
    private TotalOppsummeringPanel totalOppsummeringPanel;
    private MarkupContainer utbetalingslisteContainer;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                new ExternalLink("arenalink", arenaUtbetalingUrl + fnr),
                createFilterFormPanel(),
                totalOppsummeringPanel,
                utbetalingslisteContainer
        );
    }

    private void instansierFelter(String fnr) {
        LocalDate startDato = defaultStartDato();
        LocalDate sluttDato = defaultSluttDato();
        List<Utbetaling> utbetalinger = service.hentUtbetalinger(fnr, startDato, sluttDato);
        resultatCache = new UtbetalingsResultat(fnr, startDato, sluttDato, utbetalinger);

        filterParametere = new FilterParametere(startDato, sluttDato, true, true, hentYtelser(utbetalinger));

        totalOppsummeringPanel = createTotalOppsummeringPanel(on(utbetalinger).filter(filterParametere).collect());
        utbetalingslisteContainer = (MarkupContainer) new WebMarkupContainer("utbetalingslisteContainer")
                .add(createMaanedsPanelListe())
                .setOutputMarkupId(true);
    }

    private void oppdaterCacheOmNodvendig() {
        DateTime cacheStartDato = resultatCache.startDato.toDateTimeAtStartOfDay();
        DateTime cacheSluttDato = resultatCache.sluttDato.toDateTime(new LocalTime(23,59));
        DateTime filterStartDato = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSluttDato = filterParametere.getSluttDato().toDateTime(new LocalTime(23,59));
        if (!new Interval(cacheStartDato, cacheSluttDato).contains(new Interval(filterStartDato, filterSluttDato))) {
            List<Utbetaling> utbetalinger = service.hentUtbetalinger(resultatCache.fnr, filterParametere.getStartDato(), filterParametere.getSluttDato());
            resultatCache = new UtbetalingsResultat(resultatCache.fnr, filterParametere.getStartDato(), filterParametere.getSluttDato(), utbetalinger);
        }
    }

    private FilterFormPanel createFilterFormPanel() {
        return (FilterFormPanel) new FilterFormPanel("filterFormPanel", filterParametere).setOutputMarkupId(true);
    }

    private TotalOppsummeringPanel createTotalOppsummeringPanel(List<Utbetaling> liste) {
        return (TotalOppsummeringPanel) new TotalOppsummeringPanel(
                "totalOppsummeringPanel",
                new OppsummeringVM(liste, filterParametere.getStartDato(), filterParametere.getSluttDato()))
                .setOutputMarkupPlaceholderTag(true);
    }

    private ListView<List<Utbetaling>> createMaanedsPanelListe() {
        List<List<Utbetaling>> maanedsListe = splittUtbetalingerPerMaaned(on(resultatCache.utbetalinger).filter(filterParametere).collect());
        return new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject()));
                item.add(visibleIf(new Model<>(!item.getModelObject().isEmpty())));
            }
        };
    }

    @RunOnEvents(ENDRET)
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        oppdaterCacheOmNodvendig();

        filterParametere.setYtelser(hentYtelser(hentUtbetalingerFraPeriode(resultatCache.utbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato())));
        sendYtelserEndretEvent();
        oppdaterSynligeUtbetalinger();

        target.add(totalOppsummeringPanel, utbetalingslisteContainer.addOrReplace(createMaanedsPanelListe()));
    }

    @RunOnEvents(FEIL)
    private void skjulSnurrepippVedFeil(AjaxRequestTarget target) {
        target.add(totalOppsummeringPanel);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    private void ekspanderValgtDetaljPanel(AjaxRequestTarget target, FeedItemPayload payload) {
        target.appendJavaScript("$('#detaljpanel-" + payload.getItemId() + "').animate({height: 'toggle'}, 900);");
    }

    private void oppdaterSynligeUtbetalinger() {
        List<Utbetaling> synligeUtbetalinger = on(resultatCache.utbetalinger).filter(filterParametere).collect();
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringVM(
                synligeUtbetalinger,
                filterParametere.getStartDato(),
                filterParametere.getSluttDato()));
        totalOppsummeringPanel.setVisibilityAllowed(!synligeUtbetalinger.isEmpty());
    }

    private void sendYtelserEndretEvent() {
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

}
