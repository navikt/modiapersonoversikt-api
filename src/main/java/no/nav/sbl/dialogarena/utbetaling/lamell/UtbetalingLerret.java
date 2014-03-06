package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned.MaanedsPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsResultat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.ComponentTag;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import static no.nav.sbl.dialogarena.utbetaling.util.IntervalUtils.getUncachedInterval;

public final class UtbetalingLerret extends Lerret {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingLerret.class);

    public static final PackageResourceReference UTBETALING_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    public static final JavaScriptResourceReference UTBETALING_LAMELL_JS = new JavaScriptResourceReference(UtbetalingLerret.class, "utbetaling.js");
    private static final String FEILMELDING_DEFAULT_KEY = "feil.utbetalinger.default";

    @Inject
    private String arenaUtbetalingUrl;
    @Inject
    private UtbetalingService service;

    private UtbetalingsResultat resultatCache;
    private FilterParametere filterParametere;
    private TotalOppsummeringPanel totalOppsummeringPanel;
    private WebMarkupContainer utbetalingslisteContainer;
    private UtbetalingerMessagePanel ingenutbetalinger;
    private UtbetalingerMessagePanel feilmelding;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                createArenaLenke(fnr),
                createFilterFormPanel(),
                totalOppsummeringPanel,
                utbetalingslisteContainer,
                ingenutbetalinger,
                feilmelding
        );
    }

    private ExternalLink createArenaLenke(String fnr) {
        return new ExternalLink("arenalink", arenaUtbetalingUrl + fnr) {
            @Override
            public void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };
    }

    private void instansierFelter(String fnr) {
        ingenutbetalinger = (UtbetalingerMessagePanel) new UtbetalingerMessagePanel("ingenutbetalinger", "feil.utbetalinger.ingen-utbetalinger", "-ikon-stjerne")
                .setOutputMarkupPlaceholderTag(true);

        feilmelding = (UtbetalingerMessagePanel) new UtbetalingerMessagePanel("feilmelding", FEILMELDING_DEFAULT_KEY, "-ikon-feil")
                .setOutputMarkupPlaceholderTag(true).setVisibilityAllowed(false);

        List<Utbetaling> utbetalinger = hentUtbetalingsListe(fnr, defaultStartDato(), defaultSluttDato());
        resultatCache = new UtbetalingsResultat(fnr, defaultStartDato(), defaultSluttDato(), utbetalinger);
        filterParametere = new FilterParametere(hentYtelser(resultatCache.utbetalinger));

        List<Utbetaling> synligeUtbetalinger = on(resultatCache.utbetalinger).filter(filterParametere).collect();
        totalOppsummeringPanel = createTotalOppsummeringPanel(synligeUtbetalinger);
        utbetalingslisteContainer = (WebMarkupContainer) new WebMarkupContainer("utbetalingslisteContainer")
                .add(createMaanedsPanelListe())
                .setOutputMarkupPlaceholderTag(true);

        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());
    }

    private List<Utbetaling> hentUtbetalingsListe(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            return service.hentUtbetalinger(fnr, startDato, sluttDato);
        } catch (ApplicationException | SystemException e) {
            LOG.warn("Noe feilet ved henting av utbetalinger for fnr {}", fnr, e);

            if (e.getId() != null) {
                feilmelding.endreMessageKey(e.getId());
            } else {
                feilmelding.endreMessageKey(FEILMELDING_DEFAULT_KEY);
            }

            feilmelding.setVisibilityAllowed(true);
            return new ArrayList<>();
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

    @Override
    public void onOpening(AjaxRequestTarget target) {
        super.onOpening(target);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void oppdaterUtbetalingsliste(AjaxRequestTarget target) {
        feilmelding.setVisibilityAllowed(false);
        oppdaterCacheOmNodvendig();
        oppdaterYtelser();

        List<Utbetaling> synligeUtbetalinger = on(resultatCache.utbetalinger).filter(filterParametere).collect();
        oppdaterUtbetalingsvisning(synligeUtbetalinger);
        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());

        target.add(totalOppsummeringPanel, ingenutbetalinger, feilmelding, utbetalingslisteContainer);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void ekspanderValgtDetaljPanel(AjaxRequestTarget target, FeedItemPayload payload) {
        filterParametere = new FilterParametere(hentYtelser(resultatCache.utbetalinger));
        addOrReplace(createFilterFormPanel());
        oppdaterUtbetalingsliste(target);
        String detaljPanelID = "detaljpanel-" + payload.getItemId();
        target.appendJavaScript("Utbetalinger.haandterDetaljPanelVisning('"+detaljPanelID + "');");
    }

    protected void oppdaterCacheOmNodvendig() {
        DateTime filterStart = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSlutt = filterParametere.getSluttDato().toDateTimeAtStartOfDay();

        Optional<Interval> interval = getUncachedInterval(new Interval(filterStart, filterSlutt), resultatCache.intervaller);

        if (interval.isSome()) {
            resultatCache = oppdaterCache(resultatCache, interval.get());
        }
    }

    protected UtbetalingsResultat oppdaterCache(UtbetalingsResultat resultatCache, Interval interval) {
        Set<Utbetaling> unikeUtbetalinger = new HashSet<>(resultatCache.utbetalinger);
        unikeUtbetalinger.addAll(hentUtbetalingsListe(resultatCache.fnr, interval.getStart().toLocalDate(), interval.getEnd().toLocalDate()));
        resultatCache.utbetalinger = new ArrayList<>(unikeUtbetalinger);

        resultatCache.intervaller.add(interval);

        return resultatCache;
    }

    protected void oppdaterYtelser() {
        filterParametere.setYtelser(hentYtelser(hentUtbetalingerFraPeriode(resultatCache.utbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato())));
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

    protected void oppdaterUtbetalingsvisning(List<Utbetaling> synligeUtbetalinger) {
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringVM(synligeUtbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato()));
        utbetalingslisteContainer.addOrReplace(createMaanedsPanelListe());
    }

    private void endreSynligeKomponenter(boolean synligeUtbetalinger) {
        if (feilmelding.isVisibilityAllowed()) {
            totalOppsummeringPanel.setVisibilityAllowed(false);
            utbetalingslisteContainer.setVisibilityAllowed(false);
            ingenutbetalinger.setVisibilityAllowed(false);
        } else {
            totalOppsummeringPanel.setVisibilityAllowed(synligeUtbetalinger);
            utbetalingslisteContainer.setVisibilityAllowed(synligeUtbetalinger);
            ingenutbetalinger.setVisibilityAllowed(!synligeUtbetalinger);
        }
    }
}
