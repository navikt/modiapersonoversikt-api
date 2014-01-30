package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.FeilmeldingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned.MaanedsPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsResultat;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;
import static org.joda.time.LocalDate.now;

public class UtbetalingLerret extends Lerret {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingLerret.class);

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    public static final JavaScriptResourceReference UTBETALING_LAMELL_JS = new JavaScriptResourceReference(UtbetalingLerret.class, "utbetaling.js");

    @Inject
    private String arenaUtbetalingUrl;
    @Inject
    private UtbetalingService service;

    private UtbetalingsResultat resultatCache;
    private FilterParametere filterParametere;
    private TotalOppsummeringPanel totalOppsummeringPanel;
    private WebMarkupContainer utbetalingslisteContainer;
    private UtbetalingerMessagePanel ingenutbetalinger;
    private FeilmeldingPanel feilmelding;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                new ExternalLink("arenalink", arenaUtbetalingUrl + fnr),
                createFilterFormPanel(),
                totalOppsummeringPanel,
                utbetalingslisteContainer,
                ingenutbetalinger,
                feilmelding
        );
    }

    private void instansierFelter(String fnr) {
        ingenutbetalinger = (UtbetalingerMessagePanel) new UtbetalingerMessagePanel("ingenutbetalinger", "ingen.utbetalinger", "-ikon-stjerne")
                .setOutputMarkupPlaceholderTag(true);

        feilmelding = (FeilmeldingPanel) new FeilmeldingPanel("feilmelding", "feil.utbetalinger", "-ikon-feil")
                .setOutputMarkupPlaceholderTag(true);

        resultatCache = hentUtbetalingsResultat(fnr, defaultStartDato(), defaultSluttDato());
        filterParametere = new FilterParametere(hentYtelser(resultatCache.utbetalinger));

        List<Utbetaling> synligeUtbetalinger = on(resultatCache.utbetalinger).filter(filterParametere).collect();
        totalOppsummeringPanel = createTotalOppsummeringPanel(synligeUtbetalinger);
        utbetalingslisteContainer = (WebMarkupContainer) new WebMarkupContainer("utbetalingslisteContainer")
                .add(createMaanedsPanelListe())
                .setOutputMarkupPlaceholderTag(true);

        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());
    }

    private void oppdaterCacheOmNodvendig() {
        DateTime cacheStartDato = resultatCache.startDato.toDateTimeAtStartOfDay();
        DateTime cacheSluttDato = resultatCache.sluttDato.toDateTimeAtStartOfDay();
        DateTime filterStartDato = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSluttDato = filterParametere.getSluttDato().toDateTimeAtStartOfDay();

        if (!new Interval(cacheStartDato, cacheSluttDato).contains(new Interval(filterStartDato, filterSluttDato))) {
            resultatCache = hentUtbetalingsResultat(resultatCache.fnr, filterParametere.getStartDato(), filterParametere.getSluttDato());
        }
    }

    private UtbetalingsResultat hentUtbetalingsResultat(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            List<Utbetaling> utbetalinger = service.hentUtbetalinger(fnr, startDato, sluttDato);
            feilmelding.setVisibilityAllowed(false);
            return new UtbetalingsResultat(fnr, startDato, sluttDato, utbetalinger);
        } catch (ApplicationException ae) {
            LOG.warn("Noe feilet ved henting av utbetalinger for fnr {}", fnr, ae);
            feilmelding.setVisibilityAllowed(true);
            LocalDate now = now();
            return resultatCache != null ? resultatCache : new UtbetalingsResultat(fnr, now, now, new ArrayList<Utbetaling>());
        }
    }

    private FilterFormPanel createFilterFormPanel() {
        return (FilterFormPanel) new FilterFormPanel("filterFormPanel", filterParametere).setOutputMarkupId(true);
    }

    private TotalOppsummeringPanel createTotalOppsummeringPanel(List<Utbetaling> liste) {
        return new TotalOppsummeringPanel("totalOppsummeringPanel", new OppsummeringVM(liste, filterParametere.getStartDato(), filterParametere.getSluttDato()));
    }

    private ListView<List<Utbetaling>> createMaanedsPanelListe() {
        List<List<Utbetaling>> maanedsListe = splittUtbetalingerPerMaaned(on(resultatCache.utbetalinger).filter(filterParametere).collectIn(new ArrayList<Utbetaling>()));
        return new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject()));
                item.add(visibleIf(new Model<>(!item.getModelObject().isEmpty())));
            }
        };
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        oppdaterCacheOmNodvendig();

        filterParametere.setYtelser(hentYtelser(hentUtbetalingerFraPeriode(resultatCache.utbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato())));
        sendYtelserEndretEvent();

        List<Utbetaling> synligeUtbetalinger = on(resultatCache.utbetalinger).filter(filterParametere).collect();
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringVM(synligeUtbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato()));
        utbetalingslisteContainer.addOrReplace(createMaanedsPanelListe());

        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());
        target.add(totalOppsummeringPanel, ingenutbetalinger, feilmelding, utbetalingslisteContainer);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void ekspanderValgtDetaljPanel(AjaxRequestTarget target, FeedItemPayload payload) {
        filterParametere = new FilterParametere(hentYtelser(resultatCache.utbetalinger));
        addOrReplace(createFilterFormPanel());
        String detaljPanelID = "detaljpanel-" + payload.getItemId();
        target.appendJavaScript("Utbetalinger.haandterDetaljPanelVisning('"+detaljPanelID + "');");
    }

    private void sendYtelserEndretEvent() {
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

    private void endreSynligeKomponenter(boolean synligeUtbetalinger) {
        if (feilmelding.isVisibilityAllowed()) {
            skjulAllUtbetalingsinfo();
        } else {
            totalOppsummeringPanel.setVisibilityAllowed(synligeUtbetalinger);
            utbetalingslisteContainer.setVisibilityAllowed(synligeUtbetalinger);
            ingenutbetalinger.setVisibilityAllowed(!synligeUtbetalinger);
        }
    }

    private void skjulAllUtbetalingsinfo() {
        totalOppsummeringPanel.setVisibilityAllowed(false);
        utbetalingslisteContainer.setVisibilityAllowed(false);
        ingenutbetalinger.setVisibilityAllowed(false);
    }
}
