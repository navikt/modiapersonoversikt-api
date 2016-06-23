package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.components.ExternalLinkWithLabel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned.MaanedsPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.intervalFromStartEndDate;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.*;

public final class UtbetalingLerret extends Lerret {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingLerret.class);

    public static final PackageResourceReference UTBETALING_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    public static final JavaScriptResourceReference UTBETALING_LAMELL_JS = new JavaScriptResourceReference(UtbetalingLerret.class, "utbetaling.js");

    private static final String FEILMELDING_DEFAULT_KEY = "feil.utbetalinger.default";

    @Inject
    private String arenaUtbetalingUrl;
    @Inject
    private UtbetalingService service;

    private FilterParametere filterParametere;
    private TotalOppsummeringPanel totalOppsummeringPanel;
    private WebMarkupContainer utbetalingslisteContainer;
    private UtbetalingerMessagePanel ingenutbetalinger;
    private UtbetalingerMessagePanel feilmelding;
    private String fnr;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(createArenaLenke(fnr),
                createFilterPanel(),
                totalOppsummeringPanel,
                utbetalingslisteContainer,
                ingenutbetalinger,
                feilmelding);
    }

    private ExternalLink createArenaLenke(String fnr) {
        return new ExternalLinkWithLabel("arenalink", arenaUtbetalingUrl + fnr, new StringResourceModel("utbetaling.lamell.arena-link-tekst", this, null)) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };
    }

    private WebMarkupContainer createFilterPanel() {
        return new FilterPanel("filterPanel", filterParametere);
    }

    private void instansierFelter(String fnr) {
        this.fnr = fnr;
        ingenutbetalinger = createIngenUtbetalingerPanel();
        ingenutbetalinger.setOutputMarkupPlaceholderTag(true);

        feilmelding = createFeilmeldingPanel();
        feilmelding.setOutputMarkupPlaceholderTag(true).setVisibilityAllowed(false);

        List<Hovedytelse> ytelser = getHovedytelseListe(fnr, defaultStartDato(), defaultSluttDato());
        filterParametere = new FilterParametere(ytelserAsText(ytelser));

        List<Hovedytelse> synligeUtbetalinger = ytelser.stream()
                .filter(hovedytelse -> filterParametere.test(hovedytelse))
                .collect(toList());
        totalOppsummeringPanel = createTotalOppsummeringPanel(synligeUtbetalinger);

        utbetalingslisteContainer = createUtbetalinglisteContainer();
        utbetalingslisteContainer.add(createMaanedsPanelListe(synligeUtbetalinger));
        utbetalingslisteContainer.setOutputMarkupPlaceholderTag(true);

        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());
    }

    private List<Hovedytelse> getHovedytelseListe(String fnr, LocalDate startDato, LocalDate sluttDato) {
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

    private TotalOppsummeringPanel createTotalOppsummeringPanel(List<Hovedytelse> liste) {
        return new TotalOppsummeringPanel("totalOppsummeringPanel", new OppsummeringVM(liste, filterParametere.getStartDato(), filterParametere.getSluttDato()));
    }

    private ListView<List<Hovedytelse>> createMaanedsPanelListe(List<Hovedytelse> hovedytelseListe) {
        Map<YearMonth, List<Hovedytelse>> yearMonthListMap = ytelserGroupedByYearMonth(hovedytelseListe.stream()
                .filter(hovedytelse -> filterParametere.test(hovedytelse))
                .collect(toList()));

        return new ListView<List<Hovedytelse>>("maanedsPaneler", new ArrayList<>(yearMonthListMap.values())) {
            @Override
            protected void populateItem(ListItem<List<Hovedytelse>> item) {
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

    protected void oppdaterYtelser(List<Hovedytelse> hovedytelser) {
        Interval intervall = intervalFromStartEndDate(filterParametere.getStartDato(), filterParametere.getSluttDato());
        filterParametere.setYtelser(ytelserAsText(hovedytelserInnenforIntervall(hovedytelser, intervall)));
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

    protected void oppdaterUtbetalingsvisning(List<Hovedytelse> synligeUtbetalinger) {
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringVM(synligeUtbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato()));
        utbetalingslisteContainer.addOrReplace(createMaanedsPanelListe(synligeUtbetalinger));
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

    private WebMarkupContainer createUtbetalinglisteContainer() {
        return new WebMarkupContainer("utbetalingslisteContainer");
    }

    private UtbetalingerMessagePanel createFeilmeldingPanel() {
        return new UtbetalingerMessagePanel("feilmelding", FEILMELDING_DEFAULT_KEY, "-ikon-feil");
    }

    private UtbetalingerMessagePanel createIngenUtbetalingerPanel() {
        return new UtbetalingerMessagePanel("ingenutbetalinger", "feil.utbetalinger.ingen-utbetalinger", "-ikon-stjerne");
    }

    private List<Hovedytelse> hentHovedytelseListe() {
        return getHovedytelseListe(fnr, filterParametere.getStartDato(), filterParametere.getSluttDato());
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void oppdaterUtbetalingsliste(AjaxRequestTarget target) {
        feilmelding.setVisibilityAllowed(false);
        DateTime filterStart = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSlutt = filterParametere.getSluttDato().plusDays(1).toDateTimeAtStartOfDay();


        List<Hovedytelse> hovedytelser = getHovedytelseListe(fnr, filterStart.toLocalDate(), filterSlutt.toLocalDate());
        oppdaterYtelser(hovedytelser);

        List<Hovedytelse> synligeUtbetalinger = hovedytelser.stream()
                .filter(hovedytelse -> filterParametere.test(hovedytelse))
                .collect(toList());
        oppdaterUtbetalingsvisning(synligeUtbetalinger);
        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());

        target.add(totalOppsummeringPanel, ingenutbetalinger, feilmelding, utbetalingslisteContainer);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }
    @SuppressWarnings("unused")
    @RunOnEvents(YTELSE_FILTER_KLIKKET)
    private void oppdaterUtbetalingslisteFraYtelsesvalg(AjaxRequestTarget target) {
        feilmelding.setVisibilityAllowed(false);
        DateTime filterStart = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSlutt = filterParametere.getSluttDato().plusDays(1).toDateTimeAtStartOfDay();


        List<Hovedytelse> hovedytelser = getHovedytelseListe(fnr, filterStart.toLocalDate(), filterSlutt.toLocalDate());

        List<Hovedytelse> synligeUtbetalinger = hovedytelser.stream()
                .filter(hovedytelse -> filterParametere.test(hovedytelse))
                .collect(toList());
        oppdaterUtbetalingsvisning(synligeUtbetalinger);
        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());

        target.add(totalOppsummeringPanel, ingenutbetalinger, feilmelding, utbetalingslisteContainer);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void ekspanderValgtDetaljPanel(AjaxRequestTarget target, FeedItemPayload payload) {
        filterParametere = new FilterParametere(ytelserAsText(hentHovedytelseListe()));
        addOrReplace(createFilterPanel());
        oppdaterUtbetalingsliste(target);
        String detaljPanelID = "detaljpanel-" + payload.getItemId();
        target.appendJavaScript("Utbetalinger.haandterDetaljPanelVisning('" + detaljPanelID + "');");
    }

    private static Set<String> ytelserAsText(List<Hovedytelse> ytelser) {
        return ytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getYtelse())
                .collect(Collectors.toSet());
    }
}
